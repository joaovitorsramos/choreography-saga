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
	public Stock save(StockEvent stockHistory) {
		logger.info("saving record {}", stockHistory);
		stockHistory = stockEventRepository.save(stockHistory);
		Stock stock = new Stock(stockHistory.getSku(), stockHistory.getAmount(), stockHistory.getBranchId());
		logger.info("calling stockService.save passing {}", stockHistory);
		stock = stockService.save(stock);
		return stock;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void process(Order order) {
		List<StockMessage> stockMessageSuccessList = new ArrayList<StockMessage>();
		List<StockMessage> stockMessageFailList = new ArrayList<StockMessage>();
		order.getOrderItems().stream().forEach((s) -> {
			StockMessage stockOrigalMessage = new StockMessage(s.getSku(), s.getAmount(), s.getBranchId(),
																order.getOrderId(), s.getOrderItemId());
			try {
				StockEvent stockEvent = new StockEvent(s.getSku(), -s.getAmount(), s.getBranchId());
				logger.info("saving record {}", stockEvent);
				stockEventRepository.save(stockEvent);
				Stock stock = new Stock(s.getSku(), -s.getAmount(), s.getBranchId());
				logger.info("calling stockService.save passing {}", stockEvent);
				stock = stockService.save(stock);
				stockMessageSuccessList.add(new StockMessage(stock.getSku(), stock.getAmount(), stock.getBranchId(),
											order.getOrderId(), s.getOrderItemId()));
			} catch (OutOfStockException e) {
				stockMessageFailList.add(stockOrigalMessage);
				logger.info("sending message {} to queue {}", stockMessageFailList, StockEventService.OUT_OF_STOCK_QUEUE_NAME);
				rabbitTemplate.convertAndSend(outOfStockQueue.getName(), stockMessageFailList);
				logger.info(e.toString());
				throw e;
			}
		});
		logger.info("sending message {} to queue {}", stockMessageSuccessList, StockEventService.STOCK_UPDATED_QUEUE_NAME);
		rabbitTemplate.convertAndSend(stockUpdatedQueue.getName(), stockMessageSuccessList);

	}

}
