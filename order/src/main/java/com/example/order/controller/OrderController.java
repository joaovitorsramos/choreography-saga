package com.example.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.domain.Order;
import com.example.order.service.OrderService;

import io.swagger.annotations.ApiOperation;

@RestController
public class OrderController {

	@Autowired
	OrderService orderService;

	@ApiOperation(value = "Find an order by its Id")
	@GetMapping("/orders/{id}")
	public Order findById(@PathVariable final String id) {
		return orderService.findById(id);
	}
	
	@ApiOperation(value = "Create an order")
	@PostMapping("/orders")
	public Order create(@RequestBody final Order order) {
		return orderService.create(order);

	}

}
