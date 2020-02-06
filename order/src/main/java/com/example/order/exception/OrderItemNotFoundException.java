package com.example.order.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Order Item not found")
public class OrderItemNotFoundException extends NullPointerException {
	
	
	private static final long serialVersionUID = -3263104804907214427L;
	
	public UUID id;
	
	@Override
	public String getMessage() {
		return "OrderItem with " + id + " not found";
	}

}
