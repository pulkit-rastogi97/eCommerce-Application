package com.udacity.eCommerce.controller;

import com.udacity.eCommerce.exception.*;
import com.udacity.eCommerce.model.persistence.entity.User;
import com.udacity.eCommerce.model.requests.CreateUserRequest;
import com.udacity.eCommerce.service.UserService;
import com.udacity.eCommerce.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	protected UserService userService;

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) throws UserNotFoundException {
		LOG.debug("UserController.findById GET request initiated with id {} ", id);
		User user = userService.findUserById(id);
		LOG.debug("UserController.findById sending response with id {} and username {}  ", user.getId(), user.getUsername());
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) throws UserNotFoundException {
		LOG.debug("UserController.findByUserName GET request initiated with username {} ", username);
		User user = userService.findUserByUsername(username);
		LOG.debug("UserController.findByUserName sending response with id {} and username {} ", user.getId(), user.getUsername());
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws UserNotCreatedException, UserNameAlreadyExistsException, PasswordCriteriaFailedException, PasswordConfirmPasswordDifferentException {
		LOG.debug("UserController.createUser POST request initiated ");
		User user = new User();
		if(createUserRequest.getPassword() == null || createUserRequest.getPassword().isEmpty() || createUserRequest.getPassword().length() <= 7) {
			LOG.warn("UserController.createUser FAILURE [Password criteria not fulfilled] ");
			throw new PasswordCriteriaFailedException(Constants.PASSWORD_CRITERIA_FAILED);
		}
		if(createUserRequest.getConfirmPassword() == null || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			LOG.warn("UserController.createUser FAILURE [Password and confirmPassword are different]");
			throw new PasswordConfirmPasswordDifferentException(Constants.PASSWORD_CONFIRM_DIFFERENT);
		}
		BeanUtils.copyProperties(createUserRequest,user);
		user = userService.create(user);
		LOG.debug("UserController.createUser sending response User with id {} and username {} ", user.getId(), user.getUsername());
		return ResponseEntity.ok(user);
	}
	
}
