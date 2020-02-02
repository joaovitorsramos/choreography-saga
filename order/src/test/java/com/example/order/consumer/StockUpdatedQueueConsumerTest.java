package com.example.order.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.order.domain.Order;
import com.example.order.domain.OrderStatus;
import com.example.order.service.OrderService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockUpdatedQueueConsumerTest {

	@Autowired
	StockUpdatedQueueConsumer stockUpdatedQueueConsumer;
	
	@MockBean
	OrderService orderService;
	
	@Test
	public void whenReceiveMessageUpdateOrderWithApprovedStatus() {
		Order receivedOrder = Order.builder().orderId("123").customerId("123_peter").walletId("123_peter").build();
		ArgumentCaptor<Order> arguments = ArgumentCaptor.forClass(Order.class);
		stockUpdatedQueueConsumer.receive(receivedOrder);
		Mockito.verify(orderService).update(arguments.capture());
		assertEquals(OrderStatus.APPROVED, arguments.getValue().getStatus());
		   
	}
}
