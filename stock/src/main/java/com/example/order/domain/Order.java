package com.example.order.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Data;

@Data
public class Order implements Serializable {

	private static final long serialVersionUID = -4375073566026914661L;

	private String orderId;
	private String customerId;
	private String walletId;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	private List<OrderItem> orderItems;

}