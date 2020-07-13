package com.udacity.eCommerce.service;

import com.udacity.eCommerce.exception.ItemNotFoundException;
import com.udacity.eCommerce.model.persistence.entity.Item;
import com.udacity.eCommerce.model.persistence.repository.ItemRepository;
import com.udacity.eCommerce.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    private final Logger LOG = LoggerFactory.getLogger(ItemService.class);

    public List<Item> getAll() {
        LOG.debug("ItemService.getAll entering ");
        List<Item> items = itemRepository.findAll();
        if(items == null || items.isEmpty())
            LOG.error("ItemService.getAll - Model[Item] is returning empty table");
        LOG.info("ItemService.getAll SUCCESS [All items retrieved]");
        LOG.debug("ItemService.getAll leaving ");
        return items;
    }

    public Item findItemById(Long id) throws ItemNotFoundException {
        LOG.debug("ItemService.findItemById entering with id {} ", id);
        Optional<Item> item = itemRepository.findById(id);
        if(item.isPresent()){
            LOG.info("ItemService.findItemById SUCCESS [Item with id {} retrieved] ", item.get().getId());
            LOG.debug("ItemService.findItemById leaving with itemId {} and itemName {} ", item.get().getId(), item.get().getName());
            return item.get();
        }
        LOG.warn("ItemService.findItemById - FAILURE [Item with itemId {} not found] ", id);
        throw new ItemNotFoundException(Constants.ITEM_NOT_FOUND);
    }

    public List<Item> findItemByName(String name) throws ItemNotFoundException {
        LOG.debug("ItemService.findItemByName entering with item name {} ", name);
        List<Item> items = itemRepository.findByName(name);
        if(items == null || items.isEmpty()){
            LOG.info("ItemService.findItemById SUCCESS [Items with name {} retrieved] ", name);
            LOG.warn("ItemService.findItemByName - FAILURE [Item with name {} not found] ", name);
            throw new ItemNotFoundException(Constants.ITEM_NOT_FOUND);
        }
        LOG.debug("ItemService.findItemByName leaving");
        return items;
    }
}
