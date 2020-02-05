package com.example.stock.service;

import org.openapitools.client.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import com.example.stock.domain.Stock;
import com.example.stock.domain.StockHistory;
import com.example.stock.exceptions.OutOfStockException;
import com.example.stock.repository.StockHistoryRepository;

@Service
public class StockHistoryService {

	Logger logger = LoggerFactory.getLogger(StockHistoryService.class);

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
	StockHistoryRepository stockHistoryRepository;

	@Transactional(propagation = Propagation.REQUIRED)
	public Stock save(StockHistory stockHistory) {
		logger.info("saving record {}", stockHistory);
		stockHistory = stockHistoryRepository.save(stockHistory);
		Stock stock = new Stock(stockHistory.getSku(), stockHistory.getAmount(), stockHistory.getBranchId());
		logger.info("calling stockService.saveStock passing {}", stockHistory);
		stock = stockService.save(stock);
		return stock;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void processOrder(Order order) {
		try {
			order.getOrderItems().stream().forEach((s) -> {
				StockHistory stockHistory = new StockHistory(s.getSku(), -s.getAmount(), s.getBranchId());
				logger.info("saving record {}", stockHistory);
				stockHistoryRepository.save(stockHistory);
				Stock stock = new Stock(s.getSku(), -s.getAmount(), s.getBranchId());
				logger.info("calling stockService.saveStock passing {}", stockHistory);
				stock = stockService.save(stock);
			});
			logger.info("sending message {} to queue {}", order, StockHistoryService.STOCK_UPDATED_QUEUE_NAME);
			rabbitTemplate.convertAndSend(stockUpdatedQueue.getName(), order);
		} catch (OutOfStockException e) {
			logger.info("sending message {} to queue {}", order, StockHistoryService.OUT_OF_STOCK_QUEUE_NAME);
			rabbitTemplate.convertAndSend(outOfStockQueue.getName(), order);
			logger.info(e.toString());

		}

	}

}
