package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.stock.domain.Stock;
import com.example.stock.exceptions.OutOfStockException;
import com.example.stock.exceptions.StockExceptions;
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
		Stock itemInStock = stock.toBuilder().amount(100).build();
		Stock mockStockReturned = stock.toBuilder().amount(101).build();
		Stock stockExpected = stock.toBuilder().amount(101).build();
		Mockito.when(stockRepository.findById(stock.getSku())).thenReturn(Optional.of(itemInStock));
		Mockito.when(stockRepository.save(stock)).thenReturn(mockStockReturned);
		assertEquals(stockExpected, stockService.save(stock));

	}

	@Test
	public void whenSaveInvalidStockThrowOutOfStockException() {
		Stock stock = Stock.builder().sku("123_aspirin").branchId("123_paulista").amount(-101).build();
		Stock itemInStock = stock.toBuilder().amount(100).build();
		Mockito.when(stockRepository.findById(stock.getSku())).thenReturn(Optional.of(itemInStock));
		assertThrows(OutOfStockException.class, () -> stockService.save(stock));
	}

	@Test
	public void whenInvalidIdStockShouldNotBeReturned() {
		Mockito.when(stockRepository.findById("ABC123")).thenThrow(StockExceptions.class);
		assertThrows(StockExceptions.class, () -> stockService.findById("ABC123"));
	}

	@Test
	public void whenValidIdStockShouldObjectBeReturned() {
		Stock mockStockReturned = Stock.builder().sku("123_aspirin").branchId("123_paulista").amount(1).build();
		Stock stockExpected = mockStockReturned.toBuilder().build();
		Mockito.when(stockRepository.findById("123_aspirin")).thenReturn(Optional.of(mockStockReturned));
		assertEquals(stockExpected, stockService.findById("123_aspirin"));
	}

}
