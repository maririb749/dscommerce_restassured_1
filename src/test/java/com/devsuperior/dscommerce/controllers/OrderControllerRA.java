package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {
	
	private String adminUsename, adminPassword, clientUsername, clientPassword;
	private String adminToken, clientToken, invalidToken;
	
	private Long existingOrderId, nonExistingOrderId;
	
	
	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
		
		adminUsename ="alex@gmail.com";
		adminPassword = "123456";
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
		
		existingOrderId = 1L;
		nonExistingOrderId = 100L;
		
		adminToken = TokenUtil.obtainAccessToken(adminUsename, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = adminToken + "xpto";
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
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDontExistsAndAdminLogged()  {
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", nonExistingOrderId)
		.then()
			.statusCode(404);
	}
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDontExistsAndClientLogged()  {
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", nonExistingOrderId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void findByIdShouldReturnUnauthorizedWhenTokenInvalid()  {
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingOrderId)
		.then()
			.statusCode(401);
	}





}


