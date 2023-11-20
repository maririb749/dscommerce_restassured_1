package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;


@SuppressWarnings("unused")
public class ProductControllerRA {
	
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	
	private String clientToken, adminToken, invalidToken;
	
	
	private Long existingProductId, nonExistingProductId, dependentProductId;
	
	private String productName;
	
	private Map<String, Object> postProductInstance;
	
	private Map<String, Object> putProductInstance;
	
	@BeforeEach
	public void setUp(){
		baseURI = "http://localhost:8080";
		 productName = "Macbook";
		 
		 clientUsername = "maria@gmail.com";
		 clientPassword = "123456";
		 adminUsername = "alex@gmail.com";
		 adminPassword = "123456";
		 
		 clientToken = TokenUtil.obtainAccessToken(clientUsername,clientPassword);
		 adminToken =  TokenUtil.obtainAccessToken(adminUsername,adminPassword);
		 invalidToken = adminToken + "xpto";
		 
		
		 
		 postProductInstance = new HashMap<>();
		 postProductInstance.put("name", "Meu Produto");
		 postProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
		 postProductInstance.put("price", 50.0);
		 postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
		 
		  putProductInstance = new HashMap<>();
		  putProductInstance.put("name", "Produto atualizado");
		  putProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
		  putProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
		  putProductInstance.put("price", 200.0);
	
		List<Map<String, Object>> categories = new ArrayList<>();
		
		Map<String, Object> category1 = new HashMap<>();
		 category1.put("id",2);
		 
		 Map<String, Object> category2 = new HashMap<>();
		 category2.put("id",3);
		 
		 categories.add(category1);
		 categories.add(category2);
		 
		 postProductInstance.put("categories", categories);
		 putProductInstance.put("categories", categories);
		 
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
	
	@Test
	public void insertShouldReturnProductCreatedWhenAdminLogged() {
		JSONObject newProduct = new JSONObject(postProductInstance);
		
		given()
	    .header("Content-type", "application/json")
	    .header("Authorization", "Bearer " + adminToken)
	    .body(newProduct)
	    .contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
	.when()
	    .post("/products")
	.then()
	    .statusCode(201)
	    .body("name", equalTo("Meu Produto"))
	    .body("price", is(50.0F))
	    .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
	    .body("categories.id", hasItems(2,3));
		
	}
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedandinvalidName() {
		
		postProductInstance.put("name", "ab");
		
		JSONObject newProduct = new JSONObject(postProductInstance);
		
		given()
	    .header("Content-type", "application/json")
	    .header("Authorization", "Bearer " + adminToken)
	    .body(newProduct)
	    .contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
	.when()
	    .post("/products")
	.then()
	    .statusCode(422)
	    .body("errors.message[0]", equalTo("Nome precisar ter de 3 a 80 caracteres"));
		
	}
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedandinvalidDescription() {
		
		postProductInstance.put("description", "ab");
		
		JSONObject newProduct = new JSONObject(postProductInstance);
		
		given()
	    .header("Content-type", "application/json")
	    .header("Authorization", "Bearer " + adminToken)
	    .body(newProduct)
	    .contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
	.when()
	    .post("/products")
	.then()
	    .statusCode(422)
	    .body("errors.message[0]", equalTo("Descrição precisa ter no mínimo 10 caracteres"));

   }
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceNegativo() {
		
		postProductInstance.put("price", -50.0);
		
		JSONObject newProduct = new JSONObject(postProductInstance);
		
	given()
		.header("Content-type", "application/json")
	    .header("Authorization", "Bearer " + adminToken)
	    .body(newProduct)
	    .contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
	.when()
	    .post("/products")
	.then()
	    .statusCode(422)
	    .body("errors.message[0]", equalTo("O preço deve ser positivo"));

   }
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsZero() {
		
		postProductInstance.put("price", 0.0);
		
		JSONObject newProduct = new JSONObject(postProductInstance);
		
	given()
		.header("Content-type", "application/json")
	    .header("Authorization", "Bearer " + adminToken)
	    .body(newProduct)
	    .contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
	.when()
	    .post("/products")
	.then()
	    .statusCode(422)
	    .body("errors.message[0]", equalTo("O preço deve ser positivo"));

   }
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndProductHasNoCategory() {
		
		postProductInstance.put("categories", null);
		
		JSONObject newProduct = new JSONObject(postProductInstance);
		
	given()
		.header("Content-type", "application/json")
	    .header("Authorization", "Bearer " + adminToken)
	    .body(newProduct)
	    .contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
	.when()
	    .post("/products")
	.then()
	    .statusCode(422)
	    .body("errors.message[0]", equalTo("Deve ter pelo menos uma categoria"));

   }
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() {
	
		JSONObject newProduct = new JSONObject(postProductInstance);
		
	given()
		.header("Content-type", "application/json")
	    .header("Authorization", "Bearer " + clientToken)
	    .body(newProduct)
	    .contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
	.when()
	    .post("/products")
	.then()
	    .statusCode(403);
	
   }
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() {
	
		JSONObject newProduct = new JSONObject(postProductInstance);
		
	given()
		.header("Content-type", "application/json")
	    .header("Authorization", "Bearer " + invalidToken)
	    .body(newProduct)
	    .contentType(ContentType.JSON)
	    .accept(ContentType.JSON)
	.when()
	    .post("/products")
	.then()
	    .statusCode(401);
	
   }
	
	@Test
	public void deleteShouldReturnNoContentWhenAdminLogged() {
	  existingProductId = 25L;
		
	given()
		.header("Authorization", "Bearer " + adminToken)
	 .when()
	    .delete("/products/{id}", existingProductId)
	.then()
	    .statusCode(204);
	
   }
   
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExistsAndAdminLogged() {
	  nonExistingProductId = 100L;
		
	given()
		.header("Authorization", "Bearer " + adminToken)
	 .when()
	    .delete("/products/{id}", nonExistingProductId)
	.then()
	    .statusCode(404)
	    .body("error", equalTo("Recurso não encontrado"))
	   .body("status",equalTo(404));
   }
	@Test
	public void deleteShouldReturnBadRequestWhenDependentIdAndAdminLogged() {
		dependentProductId = 3L;
		
	given()
		.header("Authorization", "Bearer " + adminToken)
	 .when()
	    .delete("/products/{id}", dependentProductId)
	.then()
	    .statusCode(400);
	
	
   }
	
	@Test
	public void deleteShouldReturnForbiddenWhenDependentIdAndClientLogged() {
		existingProductId = 25L;
		
	given()
		.header("Authorization", "Bearer " + clientToken)
	 .when()
	    .delete("/products/{id}", existingProductId)
	.then()
	    .statusCode(403);
	
   }
	@Test
	public void deleteShouldReturnUnauthorizedWhenInvalidToken() {
		existingProductId = 25L;
		
	given()
		.header("Authorization", "Bearer " + invalidToken)
	 .when()
	    .delete("/products/{id}", existingProductId)
	.then()
	    .statusCode(401);
	
   }
	@Test
	public void updateShouldReturnProductWhenWhenIdExistsAndAdminLogged(){
		JSONObject product = new JSONObject(putProductInstance);
		existingProductId = 10L;
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(product)
		.when()
			.put("/products/{id}", existingProductId)
		.then()
			.statusCode(200)
			.body("name", equalTo("Produto atualizado"))
			.body("price", is(200.0f))
			.body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
			.body("categories.id", hasItems(2, 3))
			.body("categories.name", hasItems("Eletrônicos", "Computadores"));
   }
	@Test
	public void updateShouldReturnNotFoundWhenWhenIdNonExistsAndAdminLogged(){
		JSONObject product = new JSONObject(putProductInstance);
		nonExistingProductId = 100L;
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(product)
		.when()
			.put("/products/{id}", nonExistingProductId)
		.then()
			.statusCode(404)
		    .body("error", equalTo("Recurso não encontrado"))
		    .body("status", equalTo(404));
			
   }
	@Test
	public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidName(){
		putProductInstance.put("name", "ab");
		JSONObject product = new JSONObject(putProductInstance);
		existingProductId = 10L;
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(product)
		.when()
		    .put("/products/{id}", existingProductId)
		.then()
			.statusCode(422)
		    .body("errors.message[0]", equalTo("Nome precisar ter de 3 a 80 caracteres"))
		    .body("status", equalTo(422));
			
   }
		
}
	



	
	


