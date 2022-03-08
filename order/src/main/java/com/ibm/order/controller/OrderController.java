package com.ibm.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.order.model.JsonObject;
import com.ibm.order.model.Order;
import com.ibm.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	public ResponseEntity<Order> receiveOrders(@RequestBody String orderRequest) throws Exception, JsonProcessingException {
		log.info("Received the request from Login MS to the Order MS => " + orderRequest);
		String jsonFromJwt = getRequestFromTheJwtToken(orderRequest);
		JsonObject value= new ObjectMapper().readValue(jsonFromJwt, JsonObject.class);
		String originalRequest = value.getSub();

		Order ordRequest = new ObjectMapper().readValue(originalRequest, Order.class);
		ordRequest.setOrderDate(LocalDateTime.now());
		Order orderResponse = orderService.addOrder(ordRequest);
		log.info("Here's the response from order MS => " + orderResponse);
		return new ResponseEntity<Order>(orderResponse, HttpStatus.OK);
	}

	public String getRequestFromTheJwtToken(String jwtToken) {
		String[] splitToken = jwtToken.split("\\.");
		String encodedBody = splitToken[1];
		Base64 base64Url = new Base64(true);
		String body = new String(base64Url.decode(encodedBody));
		return body;
	}
}