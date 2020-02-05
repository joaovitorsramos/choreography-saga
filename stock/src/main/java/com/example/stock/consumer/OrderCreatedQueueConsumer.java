package com.example.stock.consumer;

import org.openapitools.client.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.UnexpectedRollbackException;


import com.example.stock.service.StockHistoryService;
import com.example.stock.service.StockService;

@Component
public class OrderCreatedQueueConsumer {
	Logger logger = LoggerFactory.getLogger(OrderCreatedQueueConsumer.class);

	@Autowired
	StockService stockService;

	@Autowired
	StockHistoryService stockHistoryService;

	@RabbitListener(queues = StockService.ORDER_CREATED_QUEUE_NAME)
	public void receive(final Order order) {
		logger.info("Message {} received in the queue {}", order, StockService.ORDER_CREATED_QUEUE_NAME);
		try {
			stockHistoryService.processOrder(order);
		} catch (UnexpectedRollbackException e) {
			logger.error( e.toString());
		}

	}
}
