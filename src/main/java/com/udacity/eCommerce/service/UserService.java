package com.udacity.eCommerce.service;

import com.udacity.eCommerce.exception.UserNameAlreadyExistsException;
import com.udacity.eCommerce.exception.UserNotCreatedException;
import com.udacity.eCommerce.exception.UserNotFoundException;
import com.udacity.eCommerce.model.persistence.entity.Cart;
import com.udacity.eCommerce.model.persistence.entity.User;
import com.udacity.eCommerce.model.persistence.repository.CartRepository;
import com.udacity.eCommerce.model.persistence.repository.UserRepository;
import com.udacity.eCommerce.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private PasswordEncoder bCryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        LOG.debug("UserService.loadUserByUsername entering with username {}", username);
        User user = userRepository.findByUsername(username);
        if(user == null){
            LOG.warn("UserService.loadUserByUsername FAILURE (User with username {} not found) ", username);
            throw new UsernameNotFoundException(username);
        }
        LOG.info("UserService.loadUserByUsername SUCCESS [User with username {} retrieved]");
        LOG.debug("UserService.loadUserByUsername leaving with userId {} and username {} ", user.getId(), user.getUsername());
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), Collections.emptyList());
    }

    public User findUserById(Long id) throws UserNotFoundException {
        LOG.debug("UserService.findUserById entering with id {}", id);
        User user = userRepository.findById(id).orElse(null);
        if(ObjectUtils.isEmpty(user)){
            LOG.warn("UserService.findUserById - FAILURE [User with id {} not found] ", id);
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
        LOG.info("UserService.findUserById SUCCESS [User retrieved with id {} ]", user.getId());
        LOG.debug("UserService.findUserById leaving with username {}", user.getUsername());
        return user;
    }

    public User findUserByUsername(String username) throws UserNotFoundException {
        LOG.debug("UserService.findUserByUsername entering with username {}", username);
        User user = userRepository.findByUsername(username);
        if(ObjectUtils.isEmpty(user)){
            LOG.warn("UserService.findUserByUsername - FAILURE [User with username {} not found] ", username);
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
        LOG.info("UserService.findUserByUsername SUCCESS [User retrieved with username {} ]", user.getUsername());
        LOG.debug("UserService.findUserByUsername leaving with userId {}", user.getId());
        return user;
    }

    public User create(User user) throws UserNotCreatedException, UserNameAlreadyExistsException {
        LOG.debug("UserService.create entering with userId {} and username {} ", user.getId(), user.getUsername());
        User userAlready = userRepository.findByUsername(user.getUsername());
        if(!ObjectUtils.isEmpty(userAlready)){
            LOG.warn("UserService.create - FAILURE [User with username {} already exists] ", user.getUsername());
            throw new UserNameAlreadyExistsException(Constants.USERNAME_ALREADY_EXISTS);
        }
        Cart cart = cartRepository.save(new Cart());
        user.setCart(cart);
        user.setPassword(bCryptEncoder.encode(user.getPassword()));
        user = userRepository.saveAndFlush(user);
        if(user.getId() <= 0){
            LOG.error("UserService.create - FAILURE [Model [User] Error creating user returned id {}] ", user.getId());
            throw new UserNotCreatedException(Constants.USER_NOT_CREATED);
        }
        LOG.info("UserService.create SUCCESS [User with username {} created with id {} ]", user.getUsername(), user.getId());
        LOG.debug("UserService.create leaving with userId {} and username {} ", user.getId(), user.getUsername());
        return user;

    }
}
