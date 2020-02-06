package com.example.order.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import com.example.order.exception.OrderNotFoundException;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import com.example.stock.domain.StockMessage;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTests {

	@Autowired
	OrderService orderService;

	@MockBean
	OrderRepository orderRepository;

	@MockBean
	OrderItemRepository orderItemRepository;

	@MockBean
	RabbitTemplate rabbitTemplate;

	private List<OrderItem> orderItemsList = new ArrayList<>();

	@BeforeEach
	public void createOrderItemsList() {
		orderItemsList.add(new OrderItem("123_aspirin", 100, "123", 100.00));
		orderItemsList.add(new OrderItem("456_ibuprofen", 200, "123", 100.00));

	}

	@Test
	public void whenInvalidIdOrderShouldNotBeFound() {
		Mockito.when(orderRepository.findById("AAA")).thenThrow(OrderNotFoundException.class);
		assertThrows(OrderNotFoundException.class, () -> orderService.findById("AAA"));

	}

	@Test
	public void whenValidIdOrderShouldBeFound() {
		Order order = Order.builder().orderId("123").walletId("123_peter").customerId("123_peter").build();
		Order mockOrderReturned = Order.builder().orderId("123").walletId("123_peter").customerId("123_peter").build();
		Mockito.when(orderRepository.findById("123")).thenReturn(Optional.of(mockOrderReturned));
		assertEquals(order, orderService.findById("123"));

	}

	@Test
	public void whenCreateOrderTheOrderStatusShouldBeApprovalPending() {
		Order order = Order.builder().orderId("123").walletId("123_peter").customerId("123_peter")
				.orderItems(orderItemsList).build();
		Order orderMock = Order.builder().orderId("123").walletId("123_peter").customerId("123_peter")
				.orderItems(orderItemsList).status(Status.APPROVAL_PENDING).build();
		Mockito.when(orderRepository.save(order)).thenReturn(orderMock);
		doNothing().when(rabbitTemplate).convertAndSend(OrderService.ORDER_CREATED_QUEUE_NAME, order);
		Order orderReturned = orderService.create(order);
		assertEquals(orderReturned, orderMock);
	}

	@Test
	public void whenProcessStockMessageListApproveOrder() {

		var stockMessageList = new ArrayList<StockMessage>();
		var orderIdList = new HashSet<String>();
		orderIdList.add("123");
		UUID orderItemUuid = UUID.randomUUID();
		stockMessageList.add(StockMessage.builder().sku("123").amount(10).branchId("123").orderId("123")
				.orderItemId(orderItemUuid).build());
		var orderItem = OrderItem.builder().sku("123").branchId("123").orderItemId(orderItemUuid)
				.itemStatus(Status.APPROVAL_PENDING).build();
		var orderItemSaved = OrderItem.builder().sku("123").branchId("123").orderItemId(orderItemUuid)
				.itemStatus(Status.APPROVED).build();
		var order = Order.builder().orderId("123").status(Status.APPROVAL_PENDING).orderItems(Arrays.asList(orderItem))
				.build();
		var orderApproved = Order.builder().orderId("123").status(Status.APPROVED)
				.orderItems(Arrays.asList(orderItemSaved)).build();
		Mockito.when(orderItemRepository.findById(any())).thenReturn(Optional.of(orderItem));
		Mockito.when(orderRepository.findById(any())).thenReturn(Optional.of(order));
		Mockito.when(orderItemRepository.save(any())).thenReturn(orderItemSaved);
		Mockito.when(orderRepository.save(any())).thenReturn(orderApproved);

		orderService.processStockUpdated(stockMessageList);
		Mockito.verify(orderRepository).save(order);

	}

}
