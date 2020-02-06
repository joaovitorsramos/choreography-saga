package com.example.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Order Item not found")
public class OrderItemNotFoundException extends NullPointerException {
	
	private static final long serialVersionUID = -3263104804907214427L;

}
