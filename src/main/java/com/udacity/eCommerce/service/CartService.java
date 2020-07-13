package com.udacity.eCommerce.service;

import com.udacity.eCommerce.exception.InsufficientQuantityException;
import com.udacity.eCommerce.exception.ItemNotFoundException;
import com.udacity.eCommerce.exception.UserNotFoundException;
import com.udacity.eCommerce.model.persistence.entity.Cart;
import com.udacity.eCommerce.model.persistence.entity.Item;
import com.udacity.eCommerce.model.persistence.entity.User;
import com.udacity.eCommerce.model.persistence.repository.CartRepository;
import com.udacity.eCommerce.model.persistence.repository.ItemRepository;
import com.udacity.eCommerce.model.persistence.repository.UserRepository;
import com.udacity.eCommerce.model.requests.ModifyCartRequest;
import com.udacity.eCommerce.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final Logger LOG = LoggerFactory.getLogger(CartService.class);


    public Cart addItem(ModifyCartRequest request) throws UserNotFoundException, ItemNotFoundException {
        LOG.debug("CartService.addItem entering with itemId {} and quantity {} " , request.getItemId(), request.getQuantity());
        User user = userRepository.findByUsername(request.getUsername());
        if(user == null) {
            LOG.warn("CartService.addItem - FAILURE [User with username {} not found] ", request.getUsername());
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if(!item.isPresent()) {
            LOG.warn("CartService.addItem - FAILURE [Item with itemId {} not found] ", request.getItemId());
            throw new ItemNotFoundException(Constants.ITEM_NOT_FOUND);
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item.get()));

        Cart savedCart = cartRepository.saveAndFlush(cart);
        LOG.info("CartService.addItem SUCCESS [Cart for User with userId{} has added item with item id {} having quantity {} ", user.getId(), item.get().getId(), request.getQuantity());
        LOG.debug("CartService.addItem leaving with username {} " , savedCart.getUser().getUsername());
        return savedCart;
    }

    public Cart removeItem(ModifyCartRequest request) throws UserNotFoundException, ItemNotFoundException, InsufficientQuantityException {
        LOG.debug("CartService.removeItem entering with itemId {} and quantity {} " , request.getItemId(), request.getQuantity());
        User user = userRepository.findByUsername(request.getUsername());
        if(user == null) {
            LOG.warn("CartService.removeItem - FAILURE [User with username {} not found] ", request.getUsername());
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if(!item.isPresent()) {
            LOG.warn("CartService.removeItem - FAILURE [Item with itemId {} not found] ", request.getItemId());
            throw new ItemNotFoundException(Constants.ITEM_NOT_FOUND);
        }
        Cart cart = user.getCart();
        List<Item> cartItems = cart.getItems();
        boolean itemFound = false;
        int itemCount = 0;
        for(Item cartItem : cartItems){
            if(cartItem.getId() == request.getItemId()){
                itemFound = true;
                itemCount++;
            }
        }
        if(!itemFound){
            LOG.warn("CartService.removeItem - FAILURE [Item with itemId {} for User with userId {} and username {} not found] ", item.get().getId(), user.getId(), user.getUsername());
            throw new ItemNotFoundException(Constants.ITEM_NOT_FOUND);
        }
        if(itemCount < request.getQuantity()) {
            LOG.warn("CartService.removeItem - FAILURE [Item with itemId {} is having insufficient quantity {} for removal for User with userId {} and username {}'s cart] ", item.get().getId(), itemCount, user.getId(), user.getUsername());
            throw new InsufficientQuantityException(Constants.INSUFFICIENT_QUANTITY);
        }

        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item.get()));

        Cart savedCart = cartRepository.saveAndFlush(cart);
        LOG.info("CartService.removeItem SUCCESS [Cart for User with userId{} has removed item with item id {} and now has quantity {} ", user.getId(), item.get().getId(), itemCount);
        LOG.debug("CartService.removeItem leaving with username {} " , savedCart.getUser().getUsername());
        return savedCart;
    }
}
