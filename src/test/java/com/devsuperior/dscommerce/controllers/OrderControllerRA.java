package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {
	
	private String adminUsename, adminPassword, clientUsername, clientPassword;
	private String adminToken, clientToken;
	
	private Long existingOrderId;
	
	
	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
		
		adminUsename ="alex@gmail.com";
		adminPassword = "123456";
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
		existingOrderId = 1L;
		
		adminToken = TokenUtil.obtainAccessToken(adminUsename, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
	}

	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() {
	 given()
	     .header("Content-type", "application/json")
	     .header("Authorization", "Bearer " + adminToken)
	     .accept(ContentType.JSON)
	 .when()
	     .get("/orders/{id}", existingOrderId)
	 .then()
	     .statusCode(200)
	     .body("id",is (1)) 
	     .body("moment",equalTo("2022-07-25T13:00:00Z"))
	     .body("status",equalTo("PAID"))
	     .body("client.name",equalTo("Maria Brown"))
	     .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
	     .body("items[0].name", equalTo("The Lord of the Rings"))
	     .body("items[1].name", equalTo("Macbook Pro"))
	     .body("total",is(1431.0F));
	}
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged() {
		existingOrderId = 3L;
	 given()
	     .header("Content-type", "application/json")
	     .header("Authorization", "Bearer " + clientToken)
	     .accept(ContentType.JSON)
	 .when()
	     .get("/orders/{id}", existingOrderId)
	 .then()
	     .statusCode(200)
	     .body("id",is (3)) 
	     .body("moment",equalTo("2022-08-03T14:20:00Z"))
	     .body("status",equalTo("WAITING_PAYMENT"))
	     .body("client.name",equalTo("Maria Brown"))
	     .body("payment.moment", equalTo(null))
	     .body("items[0].name", equalTo("The Lord of the Rings"))
	     .body("items[0].quantity",is(1))
	     .body("items[0].imgUrl",equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
	     .body("total",is(90.5F));
	}
	@Test
	public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser()  {
		Long otherOrderId = 2L;
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", otherOrderId)
		.then()
			.statusCode(403);
	}



}


