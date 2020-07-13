package com.udacity.eCommerce.controller;

import com.udacity.eCommerce.exception.InsufficientQuantityException;
import com.udacity.eCommerce.exception.ItemNotFoundException;
import com.udacity.eCommerce.exception.UserNotFoundException;
import com.udacity.eCommerce.model.persistence.entity.Cart;
import com.udacity.eCommerce.model.requests.ModifyCartRequest;
import com.udacity.eCommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	protected CartService cartService;

	private static final Logger LOG = LoggerFactory.getLogger(CartController.class);

	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) throws UserNotFoundException, ItemNotFoundException {
		LOG.debug("CartController.addToCart POST request initiated");
		Cart cart = cartService.addItem(request);
		LOG.debug("CartController.addToCart sending response");
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) throws UserNotFoundException, ItemNotFoundException, InsufficientQuantityException {
		LOG.debug("CartController.removeFromCart POST request initiated");
		Cart updatedCart = cartService.removeItem(request);
		LOG.debug("CartController.removeFromCart sending response");
		return ResponseEntity.ok(updatedCart);
	}
		
}
