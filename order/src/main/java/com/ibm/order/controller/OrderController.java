package com.ibm.order.controller;

import com.ibm.order.model.Order;
import com.ibm.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/add")
@CrossOrigin("*")
public class OrderController {
	@Autowired
	OrderService orderService;

	@PostMapping(value="/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> receiveOrder(@RequestBody Order orderRequest) {
		orderRequest.setOrderDate(LocalDateTime.now());
		Order orderResponse = orderService.addOrder(orderRequest);
		return new ResponseEntity<Order>(orderResponse, HttpStatus.OK);
	}
}
