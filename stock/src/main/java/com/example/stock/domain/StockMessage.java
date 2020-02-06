package com.example.stock.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder (toBuilder = true)
public class StockMessage {
	private String sku;
	private Integer amount;
	private String branchId;
	
	private String orderId;
	private UUID orderItemId;

}