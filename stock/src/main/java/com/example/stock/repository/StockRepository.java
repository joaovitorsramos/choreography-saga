package com.example.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.stock.domain.Stock;
import com.example.stock.domain.StockId;

@Repository
public interface StockRepository extends CrudRepository<Stock, StockId> {

	Optional<List<Stock>> findByBranchIdIn(List<String> branchIdList);
	Optional<List<Stock>> findBySku(String skuList);
	Optional<List<Stock>> findBySkuAndBranchIdIn(String id,List<String> branchIdList);
}
