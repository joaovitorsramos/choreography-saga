package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.stock.domain.Stock;
import com.example.stock.domain.StockId;
import com.example.stock.exceptions.OutOfStockException;
import com.example.stock.repository.StockRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StockServiceTests {

	@MockBean
	StockRepository stockRepository;

	@Autowired
	StockService stockService;

	@Test
	public void whenSaveValidStockReturnStockObject() {
		Stock stock = Stock.builder().sku("123_aspirin").branchId("123").amount(1).build();
		Stock itemInStock = stock.toBuilder().amount(100).build();
		Stock mockStockReturned = stock.toBuilder().amount(101).build();
		Stock stockExpected = stock.toBuilder().amount(101).build();
		Mockito.when(stockRepository.findById(new StockId(stock.getSku(), stock.getBranchId()))).thenReturn(Optional.of(itemInStock));
		Mockito.when(stockRepository.save(stock)).thenReturn(mockStockReturned);
		assertEquals(stockExpected, stockService.save(stock));

	}

	@Test
	public void whenSaveInvalidStockThrowOutOfStockException() {
		Stock stock = Stock.builder().sku("123_aspirin").branchId("123").amount(-101).build();
		Stock itemInStock = stock.toBuilder().amount(100).build();
		Mockito.when(stockRepository.findById(new StockId(stock.getSku(), stock.getBranchId()))).thenReturn(Optional.of(itemInStock));
		assertThrows(OutOfStockException.class, () -> stockService.save(stock));
	}

	
	@Test
	public void whenValidSkuStockShouldBeReturned() {
		Stock mockStockReturned = Stock.builder().sku("123_aspirin").branchId("123").amount(1).build();
		List<Stock> stockExpectedList = new ArrayList<Stock>(Arrays.asList(mockStockReturned));
		Mockito.when(stockRepository.findBySku("123_aspirin")).thenReturn(Optional.of(stockExpectedList));
		assertEquals(stockExpectedList, stockService.findBySku("123_aspirin"));
	}
	
	@Test
	public void whenValidBranchIdListStockListShouldBeReturned() {
		Stock mockStockReturned = Stock.builder().sku("123_aspirin").branchId("123").amount(1).build();
		List<Stock> stockExpectedList = new ArrayList<Stock>(Arrays.asList(mockStockReturned));
		Mockito.when(stockRepository.findByBranchIdIn(Arrays.asList("123_aspirin"))).thenReturn(Optional.of(stockExpectedList));
		assertEquals(stockExpectedList, stockService.findByBranchIds(Arrays.asList("123_aspirin")));
	}
	
	@Test
	public void whenValidSkuWithBranchIdListStockListShouldBeReturned() {
		Stock mockStockReturned = Stock.builder().sku("123_aspirin").branchId("123").amount(1).build();
		List<Stock> stockExpectedList = new ArrayList<Stock>(Arrays.asList(mockStockReturned));
		Mockito.when(stockRepository.findBySkuAndBranchIdIn("123_aspirin",Arrays.asList("123_aspirin"))).thenReturn(Optional.of(stockExpectedList));
		assertEquals(stockExpectedList, stockService.findBySkuAndBranchIds("123_aspirin",Arrays.asList("123_aspirin")));
	}



}
