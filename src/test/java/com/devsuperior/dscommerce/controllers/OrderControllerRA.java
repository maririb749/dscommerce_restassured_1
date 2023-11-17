package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {
	
	private String adminUsename, adminPassword;
	private String adminToken;
	
	private Long existingOrderId;
	
	
	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
		
		adminUsename ="alex@gmail.com";
		adminPassword = "123456";
		existingOrderId = 1L;
		
		adminToken = TokenUtil.obtainAccessToken(adminUsename, adminPassword);
		
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


}

