package com.example.stock.consumer;

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
import com.example.stock.service.StockHistoryService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderCreatedQueueConsumerTest {

	@Autowired
	OrderCreatedQueueConsumer orderCreatedQueueConsumer;
	
	@MockBean
	StockHistoryService stockHistoryService;
	
	@Test
	public void whenReceiveMessageUpdateOrderWithApprovedStatus() {
		Order receivedOrder = Order.builder().orderId("123").customerId("123_peter").walletId("123_peter")
				.status(OrderStatus.APPROVAL_PENDING).build();
		ArgumentCaptor<Order> arguments = ArgumentCaptor.forClass(Order.class);
		orderCreatedQueueConsumer.receive(receivedOrder);
		Mockito.verify(stockHistoryService).processOrder(arguments.capture());
		assertEquals(OrderStatus.APPROVAL_PENDING, arguments.getValue().getStatus());
		System.out.println("teste");
		   
	}
}