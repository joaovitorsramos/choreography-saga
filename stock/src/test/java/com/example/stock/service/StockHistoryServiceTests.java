package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.order.domain.Order;
import com.example.order.domain.OrderItem;
import com.example.order.domain.OrderStatus;
import com.example.stock.domain.Stock;
import com.example.stock.domain.StockHistory;
import com.example.stock.exceptions.OutOfStockException;
import com.example.stock.repository.StockHistoryRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StockHistoryServiceTests {

	@Autowired
	StockHistoryService stockHistoryService;

	@MockBean
	StockHistoryRepository stockHistoryRepository;

	@MockBean
	StockService stockService;

	@MockBean
	RabbitTemplate rabbitTemplate;
	
	public List<OrderItem> orderItemsList = new ArrayList<>();

	@BeforeEach
	public void createOrderItemsList() {
		orderItemsList.add(new OrderItem("123_aspirin", 100, "123", 100.00));
		orderItemsList.add(new OrderItem("456_ibuprofen", 200, "123", 100.00));

	}

	@Test
	public void whenSaveStockHistoryReturnUpdatedStock() {
		StockHistory stockHistory = StockHistory.builder()
									.sku("123_aspirin")
									.amount(1)
									.branchId("123_paulista")
									.build();
		StockHistory mockStockHistoryReturned = stockHistory.toBuilder().build();
		
		// Assuming stock already have 100 items of "123_aspirin" SKU
		Stock mockStockReturned = new Stock(stockHistory.getSku(), stockHistory.getAmount() + 100, stockHistory.getBranchId());
		Stock expectedStockReturned = mockStockReturned.toBuilder().build();
		
		Mockito.when(stockHistoryRepository.save(stockHistory)).thenReturn(mockStockHistoryReturned);
		Mockito.when(stockService.save(any())).thenReturn(mockStockReturned);
		assertEquals(expectedStockReturned, stockHistoryService.save(stockHistory));
	}

	@Test
	public void whenProcessOrderSaveStockAndPublishMessageInQueueStockUpdated() {
		Order order = Order.builder()
						.orderId("123")
						.walletId("123_peter")
						.customerId("123_peter")
						.status(OrderStatus.APPROVAL_PENDING)
						.orderItems(orderItemsList)
						.build();
		StockHistory stockHistory1 = new StockHistory(orderItemsList.get(0).getSku(), -orderItemsList.get(0).getAmount(), orderItemsList.get(0).getBranchId());
		StockHistory stockHistory2 = new StockHistory(orderItemsList.get(1).getSku(), -orderItemsList.get(1).getAmount(), orderItemsList.get(1).getBranchId());
		
		//Let's assume we have 100 of each item in stock.
		Stock stock1 = new Stock(orderItemsList.get(0).getSku(), 100-orderItemsList.get(0).getAmount(), orderItemsList.get(0).getBranchId());
		Stock stock2 = new Stock(orderItemsList.get(1).getSku(), 100-orderItemsList.get(1).getAmount(), orderItemsList.get(1).getBranchId());
		
		Mockito.when(stockHistoryRepository.save(any())).thenReturn(stockHistory1).thenReturn(stockHistory2);
		Mockito.when(stockService.save(any())).thenReturn(stock1).thenReturn(stock2);
		doNothing().when(rabbitTemplate).convertAndSend(StockHistoryService.STOCK_UPDATED_QUEUE_NAME, order);
		stockHistoryService.processOrder(order);
		Mockito.verify(rabbitTemplate).convertAndSend(StockHistoryService.STOCK_UPDATED_QUEUE_NAME, order);
	}
	
	@Test
	public void whenProcessInvalidOrderDontSaveStockButPublishMessageInQueueOutOfStock() {
		Order order = Order.builder()
						.orderId("123")
						.walletId("123_peter")
						.customerId("123_peter")
						.status(OrderStatus.APPROVAL_PENDING)
						.orderItems(orderItemsList).build();
		StockHistory stockHistory1 = new StockHistory(orderItemsList.get(0).getSku(), -orderItemsList.get(0).getAmount(), orderItemsList.get(0).getBranchId());
		StockHistory stockHistory2 = new StockHistory(orderItemsList.get(1).getSku(), -orderItemsList.get(1).getAmount(), orderItemsList.get(1).getBranchId());
		
		//Let's assume we have 100 of each item in stock.
		Stock stock1 = new Stock(orderItemsList.get(0).getSku(), 100-orderItemsList.get(0).getAmount(), orderItemsList.get(0).getBranchId());
		
		Mockito.when(stockHistoryRepository.save(any())).thenReturn(stockHistory1).thenReturn(stockHistory2);
		Mockito.when(stockService.save(any())).thenReturn(stock1).thenThrow(OutOfStockException.class);
		doNothing().when(rabbitTemplate).convertAndSend(StockHistoryService.OUT_OF_STOCK_QUEUE_NAME, order);
		stockHistoryService.processOrder(order);
		Mockito.verify(rabbitTemplate).convertAndSend(StockHistoryService.OUT_OF_STOCK_QUEUE_NAME, order);
		
	}
	
	

}
