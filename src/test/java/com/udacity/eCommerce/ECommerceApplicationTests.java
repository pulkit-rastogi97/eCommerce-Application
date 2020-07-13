package com.udacity.eCommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.eCommerce.controller.UserController;
import com.udacity.eCommerce.model.requests.CreateUserRequest;
import com.udacity.eCommerce.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ECommerceApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private UserController userController;

	@Autowired
	private JacksonTester<CreateUserRequest> json;

	private static String Authorization_Header;
	private final HttpHeaders httpHeaders = new HttpHeaders();
	private static CreateUserRequest user;

	private static boolean  isInit = false;

	public void createUserAndLoginUser() throws Exception {
		user = getUser();
		MvcResult signUpResponse = mvc.perform(
				post("/api/user/create")
						.content(mapper.writeValueAsString(user))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("username").value(user.getUsername()))
				.andReturn();
		MvcResult loginResponse = mvc.perform(
				post("/login")
						.content(mapper.writeValueAsString(user))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		Authorization_Header = loginResponse.getResponse().getHeader("Authorization");
		isInit = true;
	}

	@BeforeEach
	public void setUp() throws Exception {
		if (!isInit)
			createUserAndLoginUser();
		httpHeaders.set(HttpHeaders.AUTHORIZATION, Authorization_Header);
	}

	//USER CONTROLLER TESTS
	@Test
	public void findByIdHappyPath() throws Exception {
		mvc.perform(get("/api/user/id/1").headers(httpHeaders))
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("username"). value(user.getUsername()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void findByIdBadPath() throws Exception {
		mvc.perform(get("/api/user/id/2").headers(httpHeaders))
				.andExpect(status().is4xxClientError())
				.andReturn();
	}

	@Test
	public void findByUserNameHappyPath() throws Exception {
		mvc.perform(get("/api/user/Pulkit").headers(httpHeaders))
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("username"). value(user.getUsername()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}


	@Test
	public void findByUserNameBadPath() throws Exception {
		mvc.perform(get("/api/user/Ankita").headers(httpHeaders))
				.andExpect(status().is4xxClientError())
				.andReturn();
	}

	@Test
	public void createUserBadPath() throws Exception{
		httpHeaders.set("Authorization",null);
		MvcResult signUpResponse = mvc.perform(
				post("/api/user/create")
						.content(mapper.writeValueAsString(user))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError())
				.andReturn();
	}

	@Test
	public void user_password_too_short() throws Exception {
		httpHeaders.set(HttpHeaders.AUTHORIZATION, Authorization_Header);
		user.setPassword("123456");
		user.setConfirmPassword("123456");
		MvcResult response = mvc.perform(
				post("/api/user/create")
						.content(mapper.writeValueAsString(user))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError())
				.andReturn();
	}

	@Test
	public void user_password_confirm_mismatch() throws Exception{
		httpHeaders.set(HttpHeaders.AUTHORIZATION, Authorization_Header);
		user.setPassword("Pulkit");
		user.setConfirmPassword("Pukit");
		MvcResult response = mvc.perform(
				post("/api/user/create")
						.content(mapper.writeValueAsString(user))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError())
				.andReturn();
	}


	private CreateUserRequest getUser() {
		CreateUserRequest user = new CreateUserRequest();
		user.setUsername("Pulkit");
		user.setPassword("pUlk!T047");
		user.setConfirmPassword("pUlk!T047");
		return user;
	}



	//ITEM CONTROLLER TESTS
	@Test
	public void getItems() throws Exception {
		mvc.perform(get("/api/item").headers(httpHeaders))
				.andExpect(jsonPath("[0].name").value("Round Widget"))
				.andExpect(jsonPath("[1].name").value("Square Widget"))
				.andExpect(status().isOk());
	}

	@Test
	public void getItemByIdHappyPath() throws Exception {
		mvc.perform(get("/api/item/1").headers(httpHeaders))
				.andExpect(jsonPath("name").value("Round Widget"))
				.andExpect(jsonPath("price").value(2.99))
				.andExpect(status().isOk());
	}

	@Test
	public void getItemByIdBadPath() throws Exception {
		mvc.perform(get("/api/item/9").headers(httpHeaders))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void getItemsByNameHappyPath() throws Exception {
		mvc.perform(get("/api/item/name/Square Widget").headers(httpHeaders))
				.andExpect(jsonPath("[0].price").value(1.99))
				.andExpect(jsonPath("[0].description").value("A widget that is square"))
				.andExpect(status().isOk())
				.andReturn();
	}

	@Test
	public void getItemsByNameBadPath() throws Exception {
		mvc.perform(get("/api/item/name/Triangle Widget").headers(httpHeaders))
				.andExpect(status().is4xxClientError())
				.andReturn();
	}

	//CART CONTROLLER TEST
	@Test
	public void addToCart() throws Exception {
		ModifyCartRequest requestBody = new ModifyCartRequest();
		requestBody.setUsername(user.getUsername());
		requestBody.setItemId(1);
		requestBody.setQuantity(2);
		mvc.perform(
				post("/api/cart/addToCart")
						.headers(httpHeaders)
						.content(mapper.writeValueAsString(requestBody))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("items").isArray())
				.andExpect(jsonPath("items").isNotEmpty())
				.andExpect(jsonPath("items[0].id").value(1))
				.andExpect(jsonPath("items[0].name").value("Round Widget"))
				.andExpect(status().isOk());
	}

	@Test
	public void addToCartBadPath() throws Exception {
		ModifyCartRequest requestBody = new ModifyCartRequest();
		requestBody.setUsername(user.getUsername());
		requestBody.setItemId(10);
		requestBody.setQuantity(2);
		mvc.perform(
				post("/api/cart/addToCart")
						.headers(httpHeaders)
						.content(mapper.writeValueAsString(requestBody))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());


	}

	@Test
	public void removeFromCart() throws Exception {
		ModifyCartRequest requestBody = new ModifyCartRequest();
		requestBody.setUsername(user.getUsername());
		requestBody.setItemId(1);
		requestBody.setQuantity(1);
		mvc.perform(
				post("/api/cart/removeFromCart")
						.headers(httpHeaders)
						.content(mapper.writeValueAsString(requestBody))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("items").isArray())
				.andExpect(jsonPath("items").isNotEmpty())
				.andExpect(jsonPath("items[0].id").value(1))
				.andExpect(jsonPath("items[0].name").value("Round Widget"))
				.andExpect(status().isOk());

	}

	@Test
	public void removeFromCartBadPath() throws Exception {
		ModifyCartRequest requestBody = new ModifyCartRequest();
		requestBody.setUsername(user.getUsername());
		requestBody.setItemId(1);
		requestBody.setQuantity(20);
		mvc.perform(
				post("/api/cart/removeFromCart")
						.headers(httpHeaders)
						.content(mapper.writeValueAsString(requestBody))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());

		requestBody.setItemId(10);
		requestBody.setQuantity(1);
		mvc.perform(
				post("/api/cart/removeFromCart")
						.headers(httpHeaders)
						.content(mapper.writeValueAsString(requestBody))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());

		requestBody.setUsername("UNKNOWN_USER");
		requestBody.setItemId(1);
		requestBody.setQuantity(20);
		mvc.perform(
				post("/api/cart/removeFromCart")
						.headers(httpHeaders)
						.content(mapper.writeValueAsString(requestBody))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}


	//ORDER CONTROLLER TESTS
	@Test
	public void submitHappyPath() throws Exception {
		ModifyCartRequest requestBody = new ModifyCartRequest();
		requestBody.setUsername(user.getUsername());
		requestBody.setItemId(1);
		requestBody.setQuantity(2);
		mvc.perform(
				post("/api/cart/addToCart")
						.headers(httpHeaders)
						.content(mapper.writeValueAsString(requestBody))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mvc.perform(
				post("/api/order/submit/" + user.getUsername())
						.headers(httpHeaders))
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("items").isArray())
				.andExpect(jsonPath("items").isNotEmpty())
				.andExpect(jsonPath("items[0].id").value(1))
				.andExpect(jsonPath("items[0].name").value("Round Widget"))
				.andExpect(jsonPath("total").value(5.98))
				.andExpect(status().isOk());
	}

	@Test
	public void getOrdersForUser() throws Exception {
		mvc.perform(
				get("/api/order/history/"  + user.getUsername())
						.headers(httpHeaders))
				.andExpect(status().isOk());
	}



}