package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class UserControllerRA {
	
	private String adminUsername, adminPassowd;
	
	private String adminToken;
	
	
	@BeforeEach
	public void SetUp() {
		baseURI = "http://localhost:8080";
		
		adminUsername = "alex@gmail.com";
		adminPassowd = "123456";
		
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassowd);
	}
	
	@Test
	  public void getshouldReturnUserWhenAdminLogged(){
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer " + adminToken)
		.accept(ContentType.JSON)
	.when()
		.get("/users/me")
	.then()
		.statusCode(200)
		.body("id", is(2))
		.body("name", equalTo("Alex Green"))
		.body("email", equalTo("alex@gmail.com"))
		.body("phone", equalTo("977777777"))
		.body("birthDate", equalTo("1987-12-13"))
		.body("roles", hasItems("ROLE_CLIENT", "ROLE_ADMIN"));
    }
		
}


