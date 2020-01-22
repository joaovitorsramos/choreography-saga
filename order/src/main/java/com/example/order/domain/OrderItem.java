package com.example.order.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class OrderItem implements Serializable {

	private static final long serialVersionUID = -6681465754935755697L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", columnDefinition = "VARCHAR(255)")
	private UUID orderItemId;

	private String sku;
	private Integer amount;
	private String branchId;
	private Double cost;

	

}
