package com.ibm.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.order.model.Order;
import com.ibm.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/add")
@CrossOrigin("*")
@Slf4j
public class OrderController {
	@Autowired
	OrderService orderService;

//	Without encoding and decoding
//	@PostMapping(value="/order", consumes = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Order> receiveOrder(@RequestBody Order orderRequest) {
//		orderRequest.setOrderDate(LocalDateTime.now());
//		Order orderResponse = orderService.addOrder(orderRequest);
//		return new ResponseEntity<Order>(orderResponse, HttpStatus.OK);
//	}

	//With encoding and decoding
	@PostMapping(value = "/orders")
	public ResponseEntity<Order> receiveOrders(@RequestBody String ordReq) throws Exception, JsonProcessingException {
		log.info("Request Received at Order MS => " + ordReq);
		String originalRequest = orderService.decodeRequest(ordReq);
		Order orderRequest = new ObjectMapper().readValue(originalRequest, Order.class);
		orderRequest.setOrderDate(LocalDateTime.now());
		Order orderResponse = orderService.addOrder(orderRequest);
		log.info("Response from order MS => " + orderResponse);
		return new ResponseEntity<Order>(orderResponse, HttpStatus.OK);
	}
}