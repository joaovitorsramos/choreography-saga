package com.example.stock.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.stock.domain.Stock;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StockRepositoryTests {

	@Autowired
	StockRepository stockRepository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void whenFindByIdThenReturnStock() {
		// given
		Stock stock = Stock.builder().sku("123_aspirin").amount(20).branchId("123_paulista").build();
		entityManager.persist(stock);
		entityManager.flush();
		// when
		Optional<Stock> stockFound = stockRepository.findById("123_aspirin");
		// then
		assertEquals(stock.getSku(), stockFound.get().getSku());

	}

}
