package com.jason.springbootmall.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jason.springbootmall.constant.ProductCategory;
import com.jason.springbootmall.dto.ProductRequest;
import com.jason.springbootmall.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

  private static final String AUTHORIZATION = "Bearer " + JwtUtil.generateToken("user1@gmail.com");

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  // 查詢商品
  @Test
  public void getProduct_success() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products/{productId}", 1);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productName", notNullValue()))
        .andExpect(jsonPath("$.category", equalTo("FOOD")))
        .andExpect(jsonPath("$.imageUrl", notNullValue()))
        .andExpect(jsonPath("$.price", notNullValue()))
        .andExpect(jsonPath("$.stock", notNullValue()))
        .andExpect(jsonPath("$.description", notNullValue()))
        .andExpect(jsonPath("$.status", equalTo("ACTIVE")))
        .andExpect(jsonPath("$.createdDate", notNullValue()))
        .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
  }

  @Test
  public void getProduct_notFound() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products/{productId}", 20000);

    mockMvc.perform(requestBuilder).andExpect(status().is(404));
  }

  // 創建商品
  @Transactional
  @Test
  public void createProduct_success() throws Exception {
    ProductRequest productRequest = new ProductRequest();
    productRequest.setProductName("test food product");
    productRequest.setCategory(ProductCategory.FOOD);
    productRequest.setImageUrl("http://test.com");
    productRequest.setPrice(100);
    productRequest.setStock(2);

    String json = objectMapper.writeValueAsString(productRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/products")
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().is(201))
        .andExpect(jsonPath("$.productName", equalTo("test food product")))
        .andExpect(jsonPath("$.category", equalTo("FOOD")))
        .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
        .andExpect(jsonPath("$.price", equalTo(100)))
        .andExpect(jsonPath("$.stock", equalTo(2)))
        .andExpect(jsonPath("$.description", nullValue()))
        .andExpect(jsonPath("$.status", equalTo("ACTIVE")))
        .andExpect(jsonPath("$.createdDate", notNullValue()))
        .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
  }

  @Transactional
  @Test
  public void createProduct_illegalArgument() throws Exception {
    ProductRequest productRequest = new ProductRequest();
    productRequest.setProductName("test food product");

    String json = objectMapper.writeValueAsString(productRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/products")
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc.perform(requestBuilder).andExpect(status().is(400));
  }

  // 更新商品
  @Transactional
  @Test
  public void updateProduct_success() throws Exception {
    ProductRequest productRequest = new ProductRequest();
    productRequest.setProductName("test food product");
    productRequest.setCategory(ProductCategory.FOOD);
    productRequest.setImageUrl("http://test.com");
    productRequest.setPrice(100);
    productRequest.setStock(2);

    String json = objectMapper.writeValueAsString(productRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.put("/products/{productId}", 3)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().is(200))
        .andExpect(jsonPath("$.productName", equalTo("test food product")))
        .andExpect(jsonPath("$.category", equalTo("FOOD")))
        .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
        .andExpect(jsonPath("$.price", equalTo(100)))
        .andExpect(jsonPath("$.stock", equalTo(2)))
        .andExpect(jsonPath("$.description", nullValue()))
        .andExpect(jsonPath("$.status", equalTo("ACTIVE")))
        .andExpect(jsonPath("$.createdDate", notNullValue()))
        .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
  }

  @Transactional
  @Test
  public void updateProduct_illegalArgument() throws Exception {
    ProductRequest productRequest = new ProductRequest();
    productRequest.setProductName("test food product");

    String json = objectMapper.writeValueAsString(productRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.put("/products/{productId}", 3)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc.perform(requestBuilder).andExpect(status().is(400));
  }

  @Transactional
  @Test
  public void updateProduct_productNotFound() throws Exception {
    ProductRequest productRequest = new ProductRequest();
    productRequest.setProductName("test food product");
    productRequest.setCategory(ProductCategory.FOOD);
    productRequest.setImageUrl("http://test.com");
    productRequest.setPrice(100);
    productRequest.setStock(2);

    String json = objectMapper.writeValueAsString(productRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.put("/products/{productId}", 20000)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc.perform(requestBuilder).andExpect(status().is(404));
  }

  // 刪除商品
  @Transactional
  @Test
  public void deleteProduct_success() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.delete("/products/{productId}", 7)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

    mockMvc.perform(requestBuilder).andExpect(status().is(204));

    RequestBuilder getDeletedProductRequest =
        MockMvcRequestBuilders.get("/products/{productId}", 7);

    mockMvc.perform(getDeletedProductRequest).andExpect(status().is(404));
  }

  @Transactional
  @Test
  public void deleteProduct_deleteNonExistingProduct() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.delete("/products/{productId}", 20000)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION);

    mockMvc.perform(requestBuilder).andExpect(status().is(204));
  }

  // 查詢商品列表
  @Test
  public void getProducts() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products");

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.limit", notNullValue()))
        .andExpect(jsonPath("$.offset", notNullValue()))
        .andExpect(jsonPath("$.total", notNullValue()))
        .andExpect(jsonPath("$.results", hasSize(5)));
  }

  @Test
  public void getProducts_filtering() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/products").param("search", "B").param("category", "CAR");

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.limit", notNullValue()))
        .andExpect(jsonPath("$.offset", notNullValue()))
        .andExpect(jsonPath("$.total", notNullValue()))
        .andExpect(jsonPath("$.results", hasSize(2)));
  }

  @Test
  public void getProducts_sorting() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/products").param("orderBy", "price").param("sort", "desc");

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.limit", notNullValue()))
        .andExpect(jsonPath("$.offset", notNullValue()))
        .andExpect(jsonPath("$.total", notNullValue()))
        .andExpect(jsonPath("$.results", hasSize(5)))
        .andExpect(jsonPath("$.results[0].productId", equalTo(6)))
        .andExpect(jsonPath("$.results[1].productId", equalTo(5)))
        .andExpect(jsonPath("$.results[2].productId", equalTo(7)))
        .andExpect(jsonPath("$.results[3].productId", equalTo(4)))
        .andExpect(jsonPath("$.results[4].productId", equalTo(2)));
  }

  @Test
  public void getProducts_invalidOrderBy_shouldReturnBadRequest() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/products").param("orderBy", "price desc");

    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  public void getProducts_snakeCaseOrderBy_shouldReturnBadRequest() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/products").param("orderBy", "created_date");

    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  public void getProducts_invalidSort_shouldReturnBadRequest() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/products").param("sort", "sideways");

    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  public void getProducts_zeroLimit_shouldReturnBadRequest() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products").param("limit", "0");

    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  public void getProducts_negativeLimit_shouldReturnBadRequest() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products").param("limit", "-1");

    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  public void getProducts_negativeOffset_shouldReturnBadRequest() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products").param("offset", "-1");

    mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
  }

  @Test
  public void getProducts_pagination() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/products").param("limit", "2").param("offset", "2");

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.limit", notNullValue()))
        .andExpect(jsonPath("$.offset", notNullValue()))
        .andExpect(jsonPath("$.total", notNullValue()))
        .andExpect(jsonPath("$.results", hasSize(2)))
        .andExpect(jsonPath("$.results[0].productId", equalTo(5)))
        .andExpect(jsonPath("$.results[1].productId", equalTo(4)));
  }
}
