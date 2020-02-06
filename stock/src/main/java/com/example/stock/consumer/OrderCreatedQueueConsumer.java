package com.example.stock.consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.UnexpectedRollbackException;

import com.example.order.domain.Order;
import com.example.stock.service.StockEventService;
import com.example.stock.service.StockService;

@Component
public class OrderCreatedQueueConsumer {
	Logger logger = LoggerFactory.getLogger(OrderCreatedQueueConsumer.class);

	@Autowired
	StockService stockService;

	@Autowired
	StockEventService stockEventService;

	@RabbitListener(queues = StockService.ORDER_CREATED_QUEUE_NAME)
	public void receive(final Order order) throws UnexpectedRollbackException{
		logger.info("Message {} received in the queue {}", order, StockService.ORDER_CREATED_QUEUE_NAME);
		try {
			stockEventService.process(order);
		} catch (UnexpectedRollbackException e) {
			logger.info("There is not enough items of {} in stock to process this order",order);
		}

	}
}
