package com.example.stock.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.stock.domain.StockEvent;

@Repository
public interface StockEventRepository extends CrudRepository <StockEvent,String> {
	


}
