package com.example.stock.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder (toBuilder = true)
@IdClass(StockId.class)
public class Stock {
	@Id
	private String sku;
	private Integer amount;
	
	@Id
	private String branchId;

}
