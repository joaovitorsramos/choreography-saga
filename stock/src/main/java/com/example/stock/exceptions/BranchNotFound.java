package com.example.stock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "SKU not found")
public class BranchNotFound extends RuntimeException {

	private static final long serialVersionUID = -7220135939668094645L;

	@Override
	public String getMessage() {
		return "Branch not found";
	}
}