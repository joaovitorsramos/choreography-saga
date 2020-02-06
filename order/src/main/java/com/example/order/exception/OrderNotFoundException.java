package com.example.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Order not found")
public class OrderNotFoundException extends NullPointerException {
	
	private static final long serialVersionUID = -3263104804907214427L;

	public String id;
	
	@Override
	public String getMessage() {
		return "Order with " + id + " not found";
	}

}
