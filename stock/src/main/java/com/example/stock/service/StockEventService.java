package com.example.stock.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.order.domain.Order;
import com.example.stock.domain.Stock;
import com.example.stock.domain.StockEvent;
import com.example.stock.domain.StockMessage;
import com.example.stock.exceptions.OutOfStockException;
import com.example.stock.repository.StockEventRepository;

@Service
public class StockEventService {

	Logger logger = LoggerFactory.getLogger(StockEventService.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public static final String ORDER_CREATED_QUEUE_NAME = "orderCreated";
	public static final String STOCK_UPDATED_QUEUE_NAME = "stockUpdated";
	public static final String OUT_OF_STOCK_QUEUE_NAME = "outOfStock";

	private Queue stockUpdatedQueue = new Queue(STOCK_UPDATED_QUEUE_NAME, true);
	private Queue outOfStockQueue = new Queue(OUT_OF_STOCK_QUEUE_NAME, true);

	@Autowired
	StockService stockService;

	@Autowired
	StockEventRepository stockEventRepository;

	@Transactional(propagation = Propagation.REQUIRED)
	public Stock save(StockEvent stockEvent) {
		logger.info("saving record {}", stockEvent);
		stockEvent = stockEventRepository.save(stockEvent);
		Stock stock = new Stock(stockEvent.getSku(), stockEvent.getAmount(), stockEvent.getBranchId());
		logger.info("calling stockService.save passing {}", stockEvent);
		stock = stockService.save(stock);
		return stock;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized void process(Order order) {
		var stockMessageSuccessList = new ArrayList<StockMessage>();
		var stockMessageFailList = new ArrayList<StockMessage>();
		order.getOrderItems().stream().forEach((s) -> {
			var stockMessage = new StockMessage(s.getSku(), s.getAmount(), s.getBranchId(), order.getOrderId(),
					s.getOrderItemId());
			try {
				var stockEvent = new StockEvent(s.getSku(), -s.getAmount(), s.getBranchId());
				logger.info("saving record {}", stockEvent);
				stockEventRepository.save(stockEvent);
				var stock = new Stock(s.getSku(), -s.getAmount(), s.getBranchId());
				logger.info("calling stockService.save passing {}", stockEvent);
				stock = stockService.save(stock);
				stockMessageSuccessList.add(new StockMessage(stock.getSku(), stock.getAmount(), stock.getBranchId(),
						order.getOrderId(), s.getOrderItemId()));
			} catch (OutOfStockException e) {
				logger.info(e.getMessage());
				stockMessageFailList.add(stockMessage);
			}
		});
		if (!stockMessageFailList.isEmpty()) {
			logger.info("sending message {} to queue {}", stockMessageFailList,
					StockEventService.OUT_OF_STOCK_QUEUE_NAME);
			rabbitTemplate.convertAndSend(outOfStockQueue.getName(), stockMessageFailList);

		} else if (!stockMessageSuccessList.isEmpty()) {
			logger.info("sending message {} to queue {}", stockMessageSuccessList,
					StockEventService.STOCK_UPDATED_QUEUE_NAME);
			rabbitTemplate.convertAndSend(stockUpdatedQueue.getName(), stockMessageSuccessList);

		}
	}

}
