package com.example.order.consumer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.order.exception.OrderNotFoundException;
import com.example.order.exception.OrderItemNotFoundException;
import com.example.order.service.OrderService;
import com.example.stock.domain.StockMessage;

@Component
public class StockUpdatedQueueConsumer {

	Logger logger = LoggerFactory.getLogger(StockUpdatedQueueConsumer.class);

	@Autowired
	OrderService orderService;

	@RabbitListener(queues = OrderService.STOCK_UPDATED_QUEUE_NAME)
	public void receive(@Payload List<StockMessage> stockMessageList) {
		logger.info("Message {} received in the queue {}", stockMessageList, OrderService.STOCK_UPDATED_QUEUE_NAME);
		try {
			orderService.processStockUpdated(stockMessageList);
		} catch (OrderItemNotFoundException | OrderNotFoundException e) {
			logger.info(e.getMessage());
		}
	}
}
