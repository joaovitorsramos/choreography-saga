package com.example.order.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Enum  that represents the OrderStatus")
public enum OrderStatus implements Serializable {
	APPROVAL_PENDING, APPROVED, REJECTED

}
