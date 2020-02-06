package com.example.order.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.order.domain.Order;
import com.example.order.domain.OrderItem;
import com.example.order.domain.Status;
import com.example.order.exception.OrderItemNotFoundException;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import com.example.stock.domain.StockMessage;

@Service
public class OrderService {

	Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public static final String ORDER_CREATED_QUEUE_NAME = "orderCreated";
	public static final String STOCK_UPDATED_QUEUE_NAME = "stockUpdated";
	public static final String OUT_OF_STOCK_QUEUE_NAME = "outOfStock";

	private Queue orderCreateQueue = new Queue(ORDER_CREATED_QUEUE_NAME, true);

	public Order create(Order order) {
		order.setStatus(Status.APPROVAL_PENDING);
		order.getOrderItems().stream().forEach(i -> i.setItemStatus(Status.APPROVAL_PENDING));
		logger.info("saving record of {}", order);
		order = orderRepository.save(order);
		logger.info("publishing message {} to queue {}", order, orderCreateQueue.getName());
		rabbitTemplate.convertAndSend(orderCreateQueue.getName(), order);
		return order;
	}

	
	public Order findById(String id) {
		return orderRepository.findById(id).orElseThrow(() ->  new OrderNotFoundException(id));
	}

	
	public void processStockUpdated(List<StockMessage> stockMessageList) {
		var orderIdList = new HashSet<String>();
		stockMessageList.stream().forEach((s) -> {
			if (s.getOrderItemId()!=null) {
			OrderItem orderItem = orderItemRepository.findById(s.getOrderItemId())
									.orElseThrow(() ->  new OrderItemNotFoundException(s.getOrderItemId()));
			orderItem.setItemStatus(Status.APPROVED);
			logger.info("updating record of {}", orderItem);
			orderItem = orderItemRepository.save(orderItem);
			orderIdList.add(s.getOrderId());
			}
		});
		orderIdList.stream().forEach((s) -> {
			Order order = orderRepository.findById(s).orElseThrow(() ->  new OrderNotFoundException(s));
			boolean allOrderItemsApproved = order.getOrderItems().stream()
					.allMatch(x -> x.getItemStatus().equals(Status.APPROVED));
			logger.info("All Items of the {} are approved? ", order, allOrderItemsApproved);
			if (allOrderItemsApproved) {
				order.setStatus(Status.APPROVED);
				logger.info("updating record of {}", order);
				order = orderRepository.save(order);
			}
		});
	}

	public void processOutOfStock(List<StockMessage> stockMessageList) {
		stockMessageList.stream().forEach((s) -> {
			if (s.getOrderId()!=null) {
			Order order = orderRepository.findById(s.getOrderId()).orElseThrow(() ->  new OrderNotFoundException(s.getOrderId()));
			order.setStatus(Status.REJECTED);
			order.getOrderItems().stream().forEach(i -> i.setItemStatus(Status.REJECTED));
			logger.info("updating record of {}", order);
			order = orderRepository.save(order);
			}
		});
	}

}
