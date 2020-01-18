package com.example.order.domain;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class OrderItem implements Serializable {

	private static final long serialVersionUID = -6681465754935755697L;
	
	private UUID orderItemId;
	private String sku;
	private Integer amount;
	private String branchId;
	private Double cost;
	

}

