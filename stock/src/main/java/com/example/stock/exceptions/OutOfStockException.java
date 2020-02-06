package com.example.stock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.stock.domain.Stock;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Out of Stock")
public class OutOfStockException extends RuntimeException {

	private static final long serialVersionUID = 6782891733486092292L;

	
	public Stock stock;
	
	@Override
	public String getMessage() {
		return "Out of Stock. Not enough items of " + stock + " in stock";
	}

}
