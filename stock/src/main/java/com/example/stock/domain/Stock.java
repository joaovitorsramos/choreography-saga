package com.example.stock.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder (toBuilder = true)
public class Stock {
	@Id
	private String sku;
	private Integer amount;
	private String branchId;

}
