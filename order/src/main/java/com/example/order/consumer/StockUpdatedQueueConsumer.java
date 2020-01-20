package com.example.order.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.order.domain.Order;
import com.example.order.domain.OrderStatus;
import com.example.order.service.OrderService;

@Component
public class StockUpdatedQueueConsumer {
	
	@Autowired
	OrderService orderService;

	@RabbitListener(queues = OrderService.STOCK_UPDATED_QUEUE_NAME)
	public void receive(@Payload Order order) {
		order.setStatus(OrderStatus.APPROVED);
		orderService.updateOrder(order);
	}

}
