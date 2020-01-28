package com.example.order.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.order.domain.Order;
import com.example.order.domain.OrderStatus;
import com.example.order.service.OrderService;

@Component
public class StockUpdatedQueueConsumer {

	Logger logger = LoggerFactory.getLogger(StockUpdatedQueueConsumer.class);

	@Autowired
	OrderService orderService;

	@RabbitListener(queues = OrderService.STOCK_UPDATED_QUEUE_NAME)
	public void receive(@Payload Order order) {
		logger.info("Message {} received in the queue {}", order, OrderService.STOCK_UPDATED_QUEUE_NAME);
		order.setStatus(OrderStatus.APPROVED);
		orderService.updateOrder(order);
	}

}
