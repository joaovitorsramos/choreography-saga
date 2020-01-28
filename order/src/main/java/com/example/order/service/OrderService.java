package com.example.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.order.domain.Order;
import com.example.order.domain.OrderStatus;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.repository.OrderRepository;

@Service
public class OrderService {

	Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public static final String ORDER_CREATED_QUEUE_NAME = "orderCreated";
	public static final String STOCK_UPDATED_QUEUE_NAME = "stockUpdated";
	public static final String OUT_OF_STOCK_QUEUE_NAME = "outOfStock";

	private Queue orderCreateQueue = new Queue(ORDER_CREATED_QUEUE_NAME, true);

	public Order createOrder(Order order) {
		order.setStatus(OrderStatus.APPROVAL_PENDING);
		logger.info("creating order {}", order);
		order = orderRepository.save(order);
		logger.info("publishing message {} to queue {}", order, orderCreateQueue.getName());
		rabbitTemplate.convertAndSend(orderCreateQueue.getName(), order);
		return order;
	}

	public Order updateOrder(Order order) {
		logger.info("updating order {}", order);
		return orderRepository.save(order);
	}

	public Order findById(String id) {
		return orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
	}

}
