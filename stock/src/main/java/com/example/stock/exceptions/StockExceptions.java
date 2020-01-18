package com.example.stock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "SKU not found")
public class StockExceptions extends RuntimeException{

	private static final long serialVersionUID = 2264950327740385433L;

	@Override
	public String getMessage() {
		return "SKU not found";
	}
}


