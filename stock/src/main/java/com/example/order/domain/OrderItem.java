package com.example.order.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem implements Serializable {

	private static final long serialVersionUID = -6681465754935755697L;

	private UUID orderItemId;

	private String sku;
	private Integer amount;
	private String branchId;
	private Double cost;

	@Enumerated(EnumType.STRING)
	private Status itemStatus;

	public OrderItem(String sku, Integer amount, String branchId, Double cost) {
		super();
		this.sku = sku;
		this.amount = amount;
		this.branchId = branchId;
		this.cost = cost;
	}

}
