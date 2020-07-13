package com.udacity.eCommerce.service;

import com.udacity.eCommerce.exception.UserNotFoundException;
import com.udacity.eCommerce.model.persistence.entity.User;
import com.udacity.eCommerce.model.persistence.entity.UserOrder;
import com.udacity.eCommerce.model.persistence.repository.OrderRepository;
import com.udacity.eCommerce.model.persistence.repository.UserRepository;
import com.udacity.eCommerce.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    private final Logger LOG = LoggerFactory.getLogger(OrderService.class);

    public List<UserOrder> findOrdersByUsername(String username) throws UserNotFoundException {
        LOG.debug("OrderService.findOrdersByUsername entering with username {} ", username);
        User user = userRepository.findByUsername(username);
        if(user == null) {
            LOG.warn("OrderService.findOrdersByUsername - FAILURE [Model[User] with username {} not found] ", username);
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
        List<UserOrder> orders = orderRepository.findByUser(user);
        LOG.info("OrderService.findOrdersByUsername SUCCESS [ fetched all orders for User with username {}] ", user.getUsername());
        LOG.debug("OrderService.findOrdersByUsername leaving with all orders for username {} ", user.getUsername());
        return orders;
    }

    public UserOrder createOrder(String username) throws UserNotFoundException {
        LOG.debug("OrderService.createOrder entering with username {} ", username);
        User user = userRepository.findByUsername(username);
        if(user == null) {
            LOG.warn("OrderService.createOrder - FAILURE [Model[User] with username {} not found] ", username);
            throw  new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
        UserOrder order = UserOrder.createFromCart(user.getCart());
        UserOrder userOrder = orderRepository.save(order);
        LOG.info("OrderService.createOrder SUCCESS [Order with orderId {} for User with username {} created] ", userOrder.getId(), username);
        LOG.debug("OrderService.createOrder leaving");
        return userOrder;
    }
}
