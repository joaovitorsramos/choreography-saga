package com.example.stock.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.domain.Stock;
import com.example.stock.domain.StockEvent;
import com.example.stock.domain.StockMessage;
import com.example.stock.service.StockEventService;
import com.example.stock.service.StockService;

@RestController
public class StockController {

	@Autowired
	StockService stockService;

	@Autowired
	StockEventService stockEventService;

	@GetMapping("/stock/{id}")
	public List<Stock> findById(@PathVariable final String id,
			@RequestParam(value = "branches", required = false) final Optional<List<String>> branchIdList) {
		List<Stock> StockList = new ArrayList<>();
		if (branchIdList.isPresent()) {
			StockList = stockService.findBySkuAndBranchIds(id, branchIdList.get());
		} else {
			StockList = stockService.findBySku(id);
		}
		return StockList;

	}

	@GetMapping("/stock")
	public List<Stock> findByBranchIds(@RequestParam("branches") final List<String> branchIdList) {
		return stockService.findByBranchIds(branchIdList);
	}

	@RequestMapping(path = "/stock", method = { RequestMethod.PUT, RequestMethod.POST })
	public Stock saveStock(@RequestBody final StockEvent stockEvent) {
		return stockEventService.save(stockEvent);
	}
	
	
	@GetMapping("/stockMessage")
	public StockMessage dumStockMessage(@RequestParam("branches") String s) {
		return null;
	}

}
