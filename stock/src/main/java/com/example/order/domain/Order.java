package com.example.order.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Order implements Serializable {
	private static final long serialVersionUID = -4375073566026914661L;
	
	private String orderId;
	private String customerId;
	private String walletId;

	@Enumerated(EnumType.STRING)
	private Status status;

	private List<OrderItem> orderItems;

	public Order(String orderId, String customerId, String walletId, List<OrderItem> orderItems) {
		super();
		this.orderId = orderId;
		this.customerId = customerId;
		this.walletId = walletId;
		this.orderItems = orderItems;
	}
	

}
