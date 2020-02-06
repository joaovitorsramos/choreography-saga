package com.example.order.consumer;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.order.service.OrderService;
import com.example.stock.domain.StockMessage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OutOfStockQueueConsumerTests {

	@Autowired
	OutOfStockQueueConsumer outOfStockQueueConsumer;

	@MockBean
	OrderService orderService;
	
	@Test
	public void whenReceiveMessageUpdateOrderWithRejectedStatus() {
		
		var stockMessageList = new ArrayList<StockMessage>();
		stockMessageList.add(StockMessage.builder().sku("123").amount(10).branchId("123").orderId("123").build());
		outOfStockQueueConsumer.receive(stockMessageList);
		Mockito.verify(orderService).processOutOfStock(stockMessageList);


	}

}
