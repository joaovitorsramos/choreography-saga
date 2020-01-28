package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.stock.domain.Stock;
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

		Stock stock = Stock.builder().sku("123_aspirin").branchId("123_paulista").amount(1).build();
		Stock itemInStock = Stock.builder().sku("123_aspirin").branchId("123_paulista").amount(100).build();
		Stock stockReturned = Stock.builder().sku("123_aspirin").branchId("123_paulista").amount(101).build();
		Stock stockExpectedReturned = Stock.builder().sku("123_aspirin").branchId("123_paulista").amount(101).build();
		Mockito.when(stockRepository.findById(stock.getSku())).thenReturn(Optional.of(itemInStock));
		Mockito.when(stockRepository.save(stock)).thenReturn(stockReturned);
		assertEquals(stockExpectedReturned, stockService.saveStock(stock));

	}

}
