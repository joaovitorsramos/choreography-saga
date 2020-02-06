package com.example.stock.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.domain.StockId;
import com.example.stock.exceptions.BranchNotFound;
import com.example.stock.exceptions.OutOfStockException;
import com.example.stock.exceptions.StockExceptions;
import com.example.stock.repository.StockRepository;

@Service
public class StockService {

	Logger logger = LoggerFactory.getLogger(StockService.class);

	public static final String ORDER_CREATED_QUEUE_NAME = "orderCreated";
	public static final String STOCK_UPDATED_QUEUE_NAME = "stockUpdated";
	public static final String OUT_OF_STOCK_QUEUE_NAME = "outOfStock";

	@Autowired
	StockRepository stockRepository;

	public List<Stock> findByBranchIds(List<String> branchIdList) {
		return stockRepository.findByBranchIdIn(branchIdList).orElseThrow(BranchNotFound::new);
	}

	public List<Stock> findBySkuAndBranchIds(String id, List<String> branchIdList) {
		return stockRepository.findBySkuAndBranchIdIn(id, branchIdList).orElseThrow(BranchNotFound::new);
	}

	public List<Stock> findBySku(String id) {
		return stockRepository.findBySku(id).orElseThrow(StockExceptions::new);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized Stock save(Stock stock) {
		Stock currentStock = stockRepository.findById(new StockId(stock.getSku(), stock.getBranchId()))
								.orElse(Stock.builder().amount(0).build());
		logger.info("currentStock retrieved from database {}", currentStock);
		Integer updatedStockAmount = currentStock.getAmount() + stock.getAmount();
		if (updatedStockAmount < 0) {
			throw new OutOfStockException(stock);
		} else {
			stock.setAmount(updatedStockAmount);
			logger.info("saving record {}", stock);
			stock = stockRepository.save(stock);
			
		}
		return stock;
	}

}
