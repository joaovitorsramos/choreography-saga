package com.example.stock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Out of Stock")
public class OutOfStockException extends RuntimeException {

	private static final long serialVersionUID = 6782891733486092292L;

	@Override
	public String getMessage() {
		return "Out of Stock. Not enough items of this SKU in stock";
	}

}
