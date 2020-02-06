package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.example.order.domain.Status;
import com.example.stock.domain.Stock;
import com.example.stock.domain.StockEvent;
import com.example.stock.domain.StockMessage;
import com.example.stock.exceptions.OutOfStockException;
import com.example.stock.repository.StockEventRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StockEventServiceTests {

	@Autowired
	StockEventService stockEventService;

	@MockBean
	StockEventRepository stockEventRepository;

	@MockBean
	StockService stockService;

	@MockBean
	RabbitTemplate rabbitTemplate;
	
	public List<OrderItem> orderItemsList = new ArrayList<>();

	@BeforeEach
	public void createOrderItemsList() {
		orderItemsList.add(OrderItem.builder().sku("123_aspirin").amount(100).branchId("123").cost(100.00).build());
		orderItemsList.add(OrderItem.builder().sku("456_ibuprofen").amount(100).branchId("123").cost(100.00).build());

	}

	@Test
	public void whenSaveStockEventReturnUpdatedStock() {
		StockEvent stockEvent = StockEvent.builder()
									.sku("123_aspirin")
									.amount(1)
									.branchId("123")
									.build();
		StockEvent mockStockEventReturned = stockEvent.toBuilder().build();
		
		// Assuming stock already have 100 items of "123_aspirin" SKU
		Stock mockStockReturned = new Stock(stockEvent.getSku(), stockEvent.getAmount() + 100, stockEvent.getBranchId());
		Stock expectedStockReturned = mockStockReturned.toBuilder().build();
		
		Mockito.when(stockEventRepository.save(stockEvent)).thenReturn(mockStockEventReturned);
		Mockito.when(stockService.save(any())).thenReturn(mockStockReturned);
		assertEquals(expectedStockReturned, stockEventService.save(stockEvent));
	}

	@Test
	public void whenProcessOrderSaveStockAndPublishMessageInQueueStockUpdated() {
		UUID orderItemId1 = UUID.randomUUID();
		UUID orderItemId2 = UUID.randomUUID();
		
		orderItemsList.get(0).setOrderItemId(orderItemId1);
		orderItemsList.get(1).setOrderItemId(orderItemId2);
		
		Order order = Order.builder()
						.orderId("123")
						.walletId("123_peter")
						.customerId("123_peter")
						.status(Status.APPROVAL_PENDING)
						.orderItems(orderItemsList).build();
		StockEvent stockEvent1 = new StockEvent(orderItemsList.get(0).getSku(), -orderItemsList.get(0).getAmount(), orderItemsList.get(0).getBranchId());
		StockEvent stockEvent2 = new StockEvent(orderItemsList.get(1).getSku(), -orderItemsList.get(1).getAmount(), orderItemsList.get(1).getBranchId());
		
		//Let's assume we have 100 of each item in stock.
		Stock stock1 = new Stock(orderItemsList.get(0).getSku(), 100-orderItemsList.get(0).getAmount(), orderItemsList.get(0).getBranchId());
		Stock stock2 = new Stock(orderItemsList.get(1).getSku(), 100-orderItemsList.get(1).getAmount(), orderItemsList.get(1).getBranchId());
		
		var stockMessageSuccessList = new ArrayList<StockMessage>();

		var stockMessage1 = new StockMessage(stock1.getSku(), stock1.getAmount(), stock1.getBranchId(),
				order.getOrderId(), orderItemId1);
		var stockMessage2 = new StockMessage(stock2.getSku(), stock2.getAmount(), stock2.getBranchId(),
				order.getOrderId(), orderItemId2);
		stockMessageSuccessList.add(stockMessage1);
		stockMessageSuccessList.add(stockMessage2);
		Mockito.when(stockEventRepository.save(any())).thenReturn(stockEvent1).thenReturn(stockEvent2);
		Mockito.when(stockService.save(any())).thenReturn(stock1).thenReturn(stock2);
		
		doNothing().when(rabbitTemplate).convertAndSend(StockEventService.STOCK_UPDATED_QUEUE_NAME, stockMessageSuccessList);
		stockEventService.process(order);
		Mockito.verify(rabbitTemplate).convertAndSend(StockEventService.STOCK_UPDATED_QUEUE_NAME, stockMessageSuccessList);
	}
	
	@Test
	public void whenProcessInvalidOrderDontSaveStockButPublishMessageInQueueOutOfStock() {
		UUID orderItemId1 = UUID.randomUUID();
		UUID orderItemId2 = UUID.randomUUID();
		orderItemsList.get(0).setOrderItemId(orderItemId1);
		orderItemsList.get(1).setOrderItemId(orderItemId2);
		Order order = Order.builder()
						.orderId("123")
						.walletId("123_peter")
						.customerId("123_peter")
						.status(Status.APPROVAL_PENDING)
						.orderItems(orderItemsList).build();
		StockEvent stockEvent1 = new StockEvent(orderItemsList.get(0).getSku(), -orderItemsList.get(0).getAmount(), orderItemsList.get(0).getBranchId());
		StockEvent stockEvent2 = new StockEvent(orderItemsList.get(1).getSku(), -orderItemsList.get(1).getAmount(), orderItemsList.get(1).getBranchId());
		
		//Let's assume we have only 100 of the first item
		Stock stock1 = new Stock(orderItemsList.get(0).getSku(), 10-orderItemsList.get(0).getAmount(), orderItemsList.get(0).getBranchId());
		var stockMessageFailedList = new ArrayList<StockMessage>();
		var stockMessage2 = new StockMessage(orderItemsList.get(1).getSku(), orderItemsList.get(1).getAmount(), orderItemsList.get(1).getBranchId(),
				order.getOrderId(), orderItemId2);
		stockMessageFailedList.add(stockMessage2);
		Mockito.when(stockEventRepository.save(any())).thenReturn(stockEvent1).thenReturn(stockEvent2);
		Mockito.when(stockService.save(any())).thenReturn(stock1).thenThrow(OutOfStockException.class);
		doNothing().when(rabbitTemplate).convertAndSend(StockEventService.OUT_OF_STOCK_QUEUE_NAME, stockMessageFailedList);
		stockEventService.process(order);
		Mockito.verify(rabbitTemplate).convertAndSend(StockEventService.OUT_OF_STOCK_QUEUE_NAME, stockMessageFailedList);
	}
	
	

}
