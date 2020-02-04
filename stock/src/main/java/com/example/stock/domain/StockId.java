package com.example.stock.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StockId implements Serializable{
	private static final long serialVersionUID = -8114130898963292601L;
	private String sku;
	private String branchId;
	
}
