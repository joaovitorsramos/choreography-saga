package com.example.order.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "order_table")
public class Order implements Serializable {
	private static final long serialVersionUID = -4375073566026914661L;

	@Id
	private String orderId;
	private String customerId;
	private String walletId;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "orderId", referencedColumnName = "orderId")
	private List<OrderItem> orderItems;
	

}
