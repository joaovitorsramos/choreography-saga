package com.example.stock.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.stock.domain.StockHistory;

@Repository
public interface StockHistoryRepository extends CrudRepository <StockHistory,String> {
	


}
