package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


@SuppressWarnings("unused")
public class ProductControllerRA {
	
	private Long existingProductId, nonExistingProductId;
	
	private String productName;
	
	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
		 productName = "Macbook";
	
		
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() {
		
		existingProductId = 2L;
		
		given()
		     .get("/products/{id}", existingProductId)
		   .then()
		     .statusCode(200)
		     .body("id",is(2))
		     .body("name",equalTo("Smart TV"))
		     .body("imgUrl",equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
		     .body("price",is(2190.0F))
		     .body("categories.id",hasItems(2,3))
		     .body("categories.name",hasItems("Eletrônicos","Computadores"));
	}
	@Test
	public void findByIdShouldReturnNotFoundWhenIdNonExists() {
		
		
		nonExistingProductId = 100L;
		
		given()
		     .get("/products/{id}", nonExistingProductId)
		  .then()
		  .statusCode(404)
		  .body("error", equalTo("Recurso não encontrado"));
   
	}
	@Test
	public void findAllShouldReturnPagedProductsWhenProductNameParamIsEmpty() {
		given()
			.get("/products?page=0")
		.then()
			.body("content.name", hasItems("The Lord of the Rings", "PC Gamer Y"));
	}
	
	@Test
	public void findAllShouldReturnPagedProductsWhenProductNameParamIsNotEmpty() {
	   
	    given()
	        .get("/products?name={productName}", productName)
	    .then()
	        .statusCode(200)
	        .body("content.id[0]", is(3))
	        .body("content.name[0]", equalTo("Macbook Pro"))
	        .body("content.price[0]", is(1250.0f))
	        .body("content.imgUrl[0]", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
	}
	@Test
	public void findAllShouldReturnPagedProductsWithPriceGreaterThan2000() {
		given()
			.get("/products?size=25")
		.then()
			.body("content.findAll { it.price > 2000 }.name", hasItems("Smart TV", "PC Gamer Weed"));
	}
	
}


	
	


