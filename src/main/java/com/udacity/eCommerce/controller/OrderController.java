package com.udacity.eCommerce.controller;

import com.udacity.eCommerce.exception.UserNotFoundException;
import com.udacity.eCommerce.model.persistence.entity.UserOrder;
import com.udacity.eCommerce.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	@Autowired
	protected OrderService orderService;

	private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) throws UserNotFoundException {
		LOG.debug("OrderController.submit POST request initiated with username {} ", username);
		UserOrder userOrder = orderService.createOrder(username);
		LOG.debug("OrderController.submit sending response");
		return ResponseEntity.ok(userOrder);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) throws UserNotFoundException {
		LOG.debug("OrderController.getOrdersForUser POST request initiated with username {} ", username);
		List<UserOrder> userOrders = orderService.findOrdersByUsername(username);
		LOG.debug("OrderController.getOrdersForUser sending response");
		return ResponseEntity.ok(userOrders);
	}
}
