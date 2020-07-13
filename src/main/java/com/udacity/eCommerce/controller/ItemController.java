package com.udacity.eCommerce.controller;

import com.udacity.eCommerce.exception.ItemNotFoundException;
import com.udacity.eCommerce.model.persistence.entity.Item;
import com.udacity.eCommerce.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	@Autowired
	protected ItemService itemService;

	private static final Logger LOG = LoggerFactory.getLogger(ItemController.class);

	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		LOG.debug("ItemController.getItems GET request initiated");
		List<Item> items = itemService.getAll();
		LOG.debug("ItemController.getItems sending response");
		return ResponseEntity.ok(items);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) throws ItemNotFoundException {
		LOG.debug("ItemController.getItemById GET request initiated with id {} ", id);
		Item item = itemService.findItemById(id);
		LOG.debug("ItemController.getItemById sending response Item with name {} ", item.getName());
		return ResponseEntity.ok(item);
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) throws ItemNotFoundException {
		LOG.debug("ItemController.getItemsByName GET request initiated with name {} ", name);
		List<Item> items = itemService.findItemByName(name);
		LOG.debug("ItemController.getItemsByName sending response");
		return ResponseEntity.ok(items);
	}
	
}
