package com.example.stock.repository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StockRepositoryTests {
	
	@Autowired
	StockRepository stockRepository;
	
	@MockBean
	TestEntityManager entityManager;
	/*
	@Test
	public void whenFindByIdThenReturnStock() {
		Stock stock = new Stock 
		
	}
*/
}
