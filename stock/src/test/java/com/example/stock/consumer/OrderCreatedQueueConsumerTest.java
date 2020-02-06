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


import com.example.stock.service.StockEventService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderCreatedQueueConsumerTest {

	@Autowired
	OrderCreatedQueueConsumer orderCreatedQueueConsumer;
	
	@MockBean
	StockEventService stockHistoryService;
/*	
	@Test
	public void whenReceiveMessageUpdateOrderWithApprovedStatus() {
		Order receivedOrder = new Order().orderId("123").customerId("123_peter").walletId("123_peter")
				.status(Order.StatusEnum.APPROVAL_PENDING);
		ArgumentCaptor<Order> arguments = ArgumentCaptor.forClass(Order.class);
		orderCreatedQueueConsumer.receive(receivedOrder);
		Mockito.verify(stockHistoryService).process(arguments.capture());
		assertEquals(Order.StatusEnum.APPROVAL_PENDING, arguments.getValue().getStatus());

	}
	*/
}