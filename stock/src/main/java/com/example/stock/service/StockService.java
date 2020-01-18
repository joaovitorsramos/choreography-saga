package com.example.stock.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.order.domain.Order;
import com.example.stock.domain.Stock;
import com.example.stock.exceptions.OutOfStockException;
import com.example.stock.exceptions.StockExceptions;
import com.example.stock.repository.StockRepository;

@Service
public class StockService {
	
	Logger logger = LoggerFactory.getLogger(StockService.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public static final String ORDER_CREATED_QUEUE_NAME = "orderCreated";
	public static final String STOCK_UPDATED_QUEUE_NAME = "stockUpdated";
	public static final String OUT_OF_STOCK_QUEUE_NAME = "outOfStock";
	
	private Queue stockUpdatedQueue = new Queue(STOCK_UPDATED_QUEUE_NAME, true);
	private Queue stockBreachedQueue = new Queue(OUT_OF_STOCK_QUEUE_NAME, true);
	
	@Autowired
	StockRepository stockRepository;
	
	public Stock findById(String id) {
		return stockRepository.findById(id).orElseThrow(StockExceptions::new);	
		
	}
	
	
	public Stock save(Stock stock) {
		
		Stock currentStock=stockRepository.findById(stock.getSku()).orElse(new Stock());
		Integer updatedStockAmount = currentStock.getAmount() + stock.getAmount();
		if(updatedStockAmount < 0) {
			throw new OutOfStockException();
			}else {
				stock.setAmount(updatedStockAmount);
				logger.info("saving stock {}", stock);
				stock = stockRepository.save(stock);
		}
		return stock;
	}
		
	
	public void decrementStockUsingOrder (Order order) {
		List<Stock> stockList = new ArrayList<Stock>();
		order.getOrderItems()
		.stream()
		.forEach(s -> stockList.add(new Stock(s.getSku(),-s.getAmount(),s.getBranchId())));
		
		try {
			stockList.forEach(s-> this.save(s));
			rabbitTemplate.convertAndSend(stockUpdatedQueue.getName(), order);
		}catch(OutOfStockException e){
			rabbitTemplate.convertAndSend(stockBreachedQueue.getName(), order);
			logger.info("Exception {}",e.toString());
		}
		
		
	}
		
		

}
