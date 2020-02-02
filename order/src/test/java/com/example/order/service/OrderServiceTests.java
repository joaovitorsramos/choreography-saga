package com.example.order.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
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
import com.example.order.exception.OrderNotFoundException;
import com.example.order.repository.OrderRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTests {

	@Autowired
	OrderService orderService;

	@MockBean
	OrderRepository orderRepository;

	@MockBean
	RabbitTemplate rabbitTemplate;

	private List<OrderItem> orderItemsList = new ArrayList<>();

	@Before
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
				.orderItems(orderItemsList).status(OrderStatus.APPROVAL_PENDING).build();
		Mockito.when(orderRepository.save(order)).thenReturn(orderMock);
		doNothing().when(rabbitTemplate).convertAndSend(OrderService.ORDER_CREATED_QUEUE_NAME, order);
		Order orderReturned = orderService.create(order);
		assertEquals(orderReturned, orderMock);
	}

	@Test
	public void whenUpdatingOrderTheOrderReturnedShouldBeTheSame() {
		Order order = Order.builder().orderId("123").walletId("123_peter").customerId("123_peter")
				.orderItems(orderItemsList).status(OrderStatus.APPROVED).build();
		Mockito.when(orderRepository.save(order)).thenReturn(order);
		Order orderReturned = orderService.update(order);
		assertEquals(orderReturned, order);
	}

}
