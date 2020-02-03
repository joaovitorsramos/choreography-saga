package com.example.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.stock.domain.Stock;

@Repository
public interface StockRepository extends CrudRepository<Stock, String> {

	Optional<List<Stock>> findByBranchIdIn(List<String> branchIdList);

}
