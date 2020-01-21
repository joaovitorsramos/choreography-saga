package com.example.stock.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockHistory {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", columnDefinition = "VARCHAR(255)")
	private UUID Id;

	private String sku;
	private Integer amount = 0;
	private String branchId;

	@CreationTimestamp
	private LocalDateTime timestamp;
	
	
	public StockHistory(String sku, int amount, String branchId) {
		this.sku = sku;
		this.amount = amount;
		this.branchId = branchId;
	}
}