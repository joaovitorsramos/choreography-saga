package com.example.order.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;

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

	public Order generateOrderForTest(OrderStatus orderStatus) {
		List<OrderItem> orderItemsList = new ArrayList<>();
		orderItemsList.add(new OrderItem("123_aspirin", 100, "Paulista", 100.00));
		orderItemsList.add(new OrderItem("456_ibuprofen", 200, "Brooklin", 100.00));
		if (orderStatus == null)
			return new Order("1111", "1111", "101010", orderItemsList);
		else
			return new Order("1111", "1111", "101010", orderStatus, orderItemsList);
	}

	/*
	@Test
	public void whenInvalidIdOrderShouldNotBeFound() {
		Mockito.when(orderRepository.findById("AAA")).thenThrow(NullPointerException.class);
		Mockito.reset(orderRepository);
		assertThrows(OrderNotFoundException.class, () -> orderRepository.findById("AAA"));

	}
*/
	@Test
	public void whenCreateOrderTheOrderStatusShouldBeApprovalPending() {
		Order order = this.generateOrderForTest(null);
		Order orderMock = this.generateOrderForTest(OrderStatus.APPROVAL_PENDING);
		Mockito.when(orderRepository.save(order)).thenReturn(orderMock);
		doNothing().when(rabbitTemplate).convertAndSend(OrderService.ORDER_CREATED_QUEUE_NAME, order);
		Order orderReturned = orderService.createOrder(order);
		assertEquals(orderReturned, orderMock);
	}

	@Test
	public void whenUpdatingOrderTheOrderReturnedShouldBeTheSame() {
		Order order = this.generateOrderForTest(OrderStatus.APPROVED);
		Mockito.when(orderRepository.save(order)).thenReturn(order);
		Order orderReturned = orderService.updateOrder(order);
		assertEquals(orderReturned, order);
	}

}
