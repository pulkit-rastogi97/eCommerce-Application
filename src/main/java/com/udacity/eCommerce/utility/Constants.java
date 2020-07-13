package com.udacity.eCommerce.utility;

public interface Constants {
     String SECRET = "SecretKeyToGenJWTs";
     long EXPIRATION_TIME = 864_000_000; // 10 days
     String TOKEN_PREFIX = "Bearer ";
     String HEADER_STRING = "Authorization";
     String USER_NOT_FOUND = "User Not Found";
     String USER_NOT_CREATED = "User Not Created";
     String ITEM_NOT_FOUND = "Item Not Found";
     String USERNAME_ALREADY_EXISTS = "Username already exists";
     String PASSWORD_CRITERIA_FAILED = "Password length should be minimum of 8 characters";
     String PASSWORD_CONFIRM_DIFFERENT = "Password and Confirm Password field should have same value";
     String INSUFFICIENT_QUANTITY = "Item quantity in cart is insufficient";
}
