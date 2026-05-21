package com.jason.springbootmall.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jason.springbootmall.dto.BuyItem;
import com.jason.springbootmall.dto.CreateOrderRequest;
import com.jason.springbootmall.util.JwtUtil;
import java.util.ArrayList;
import java.util.List;
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
public class OrderControllerTest {

  private static final String AUTHORIZATION_USER_1 =
      "Bearer " + JwtUtil.generateToken("user1@gmail.com");
  private static final String AUTHORIZATION_USER_2 =
      "Bearer " + JwtUtil.generateToken("user2@gmail.com");

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Transactional
  @Test
  public void createOrder_success() throws Exception {
    CreateOrderRequest createOrderRequest = new CreateOrderRequest();
    List<BuyItem> buyItemList = new ArrayList<>();

    BuyItem buyItem1 = new BuyItem();
    buyItem1.setProductId(1);
    buyItem1.setQuantity(5);
    buyItemList.add(buyItem1);

    BuyItem buyItem2 = new BuyItem();
    buyItem2.setProductId(2);
    buyItem2.setQuantity(2);
    buyItemList.add(buyItem2);

    createOrderRequest.setBuyItemList(buyItemList);

    String json = objectMapper.writeValueAsString(createOrderRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/users/{userId}/orders", 1)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderId", notNullValue()))
        .andExpect(jsonPath("$.userId", equalTo(1)))
        .andExpect(jsonPath("$.totalAmount", equalTo(750)))
        .andExpect(jsonPath("$.orderItemList", hasSize(2)))
        .andExpect(jsonPath("$.createdDate", notNullValue()))
        .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
  }

  @Transactional
  @Test
  public void createOrder_illegalArgument_emptyBuyItemList() throws Exception {
    CreateOrderRequest createOrderRequest = new CreateOrderRequest();
    createOrderRequest.setBuyItemList(new ArrayList<>());

    String json = objectMapper.writeValueAsString(createOrderRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/users/{userId}/orders", 1)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(400)))
        .andExpect(jsonPath("$.code", equalTo("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.message", equalTo("Request validation failed")))
        .andExpect(jsonPath("$.errors[0].field", equalTo("buyItemList")));
  }

  @Transactional
  @Test
  public void createOrder_illegalArgument_zeroQuantity() throws Exception {
    CreateOrderRequest createOrderRequest = new CreateOrderRequest();
    List<BuyItem> buyItemList = new ArrayList<>();

    BuyItem buyItem = new BuyItem();
    buyItem.setProductId(1);
    buyItem.setQuantity(0);
    buyItemList.add(buyItem);

    createOrderRequest.setBuyItemList(buyItemList);

    String json = objectMapper.writeValueAsString(createOrderRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/users/{userId}/orders", 1)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(400)))
        .andExpect(jsonPath("$.code", equalTo("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.message", equalTo("Request validation failed")))
        .andExpect(jsonPath("$.errors[0].field", equalTo("buyItemList[0].quantity")));
  }

  @Transactional
  @Test
  public void createOrder_forbidden_whenPathUserDoesNotMatchToken() throws Exception {
    CreateOrderRequest createOrderRequest = new CreateOrderRequest();
    List<BuyItem> buyItemList = new ArrayList<>();

    BuyItem buyItem = new BuyItem();
    buyItem.setProductId(1);
    buyItem.setQuantity(1);
    buyItemList.add(buyItem);

    createOrderRequest.setBuyItemList(buyItemList);

    String json = objectMapper.writeValueAsString(createOrderRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/users/{userId}/orders", 2)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status", equalTo(403)))
        .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")))
        .andExpect(jsonPath("$.message", equalTo("Cannot access another user's orders")));
  }

  @Transactional
  @Test
  public void createOrder_productNotExist() throws Exception {
    CreateOrderRequest createOrderRequest = new CreateOrderRequest();
    List<BuyItem> buyItemList = new ArrayList<>();

    BuyItem buyItem = new BuyItem();
    buyItem.setProductId(100);
    buyItem.setQuantity(1);
    buyItemList.add(buyItem);

    createOrderRequest.setBuyItemList(buyItemList);

    String json = objectMapper.writeValueAsString(createOrderRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/users/{userId}/orders", 1)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(400)))
        .andExpect(jsonPath("$.code", equalTo("BAD_REQUEST")))
        .andExpect(jsonPath("$.message", equalTo("Product does not exist")));
  }

  @Transactional
  @Test
  public void createOrder_stockNotEnough() throws Exception {
    CreateOrderRequest createOrderRequest = new CreateOrderRequest();
    List<BuyItem> buyItemList = new ArrayList<>();

    BuyItem buyItem = new BuyItem();
    buyItem.setProductId(1);
    buyItem.setQuantity(10000);
    buyItemList.add(buyItem);

    createOrderRequest.setBuyItemList(buyItemList);

    String json = objectMapper.writeValueAsString(createOrderRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/users/{userId}/orders", 1)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(400)))
        .andExpect(jsonPath("$.code", equalTo("BAD_REQUEST")))
        .andExpect(jsonPath("$.message", equalTo("Product stock is not enough")));
  }

  @Test
  public void createOrder_secondItemStockNotEnough_shouldRollbackFirstStockDecrease()
      throws Exception {
    CreateOrderRequest createOrderRequest = new CreateOrderRequest();
    List<BuyItem> buyItemList = new ArrayList<>();

    BuyItem firstItem = new BuyItem();
    firstItem.setProductId(1);
    firstItem.setQuantity(1);
    buyItemList.add(firstItem);

    BuyItem secondItem = new BuyItem();
    secondItem.setProductId(2);
    secondItem.setQuantity(10000);
    buyItemList.add(secondItem);

    createOrderRequest.setBuyItemList(buyItemList);

    String json = objectMapper.writeValueAsString(createOrderRequest);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/users/{userId}/orders", 1)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(400)))
        .andExpect(jsonPath("$.code", equalTo("BAD_REQUEST")))
        .andExpect(jsonPath("$.message", equalTo("Product stock is not enough")));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/products/{productId}", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stock", equalTo(10)));
  }

  @Test
  public void getOrders() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/users/{userId}/orders", 1)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.limit", notNullValue()))
        .andExpect(jsonPath("$.offset", notNullValue()))
        .andExpect(jsonPath("$.total", notNullValue()))
        .andExpect(jsonPath("$.results", hasSize(2)))
        .andExpect(jsonPath("$.results[0].orderId", notNullValue()))
        .andExpect(jsonPath("$.results[0].userId", equalTo(1)))
        .andExpect(jsonPath("$.results[0].totalAmount", equalTo(100000)))
        .andExpect(jsonPath("$.results[0].orderItemList", hasSize(1)))
        .andExpect(jsonPath("$.results[0].createdDate", notNullValue()))
        .andExpect(jsonPath("$.results[0].lastModifiedDate", notNullValue()))
        .andExpect(jsonPath("$.results[1].orderId", notNullValue()))
        .andExpect(jsonPath("$.results[1].userId", equalTo(1)))
        .andExpect(jsonPath("$.results[1].totalAmount", equalTo(500690)))
        .andExpect(jsonPath("$.results[1].orderItemList", hasSize(3)))
        .andExpect(jsonPath("$.results[1].createdDate", notNullValue()))
        .andExpect(jsonPath("$.results[1].lastModifiedDate", notNullValue()));
  }

  @Test
  public void getOrders_withoutJwtToken() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/{userId}/orders", 1);

    mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
  }

  @Test
  public void getOrders_pagination() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/users/{userId}/orders", 1)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1)
            .param("limit", "2")
            .param("offset", "2");

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.limit", notNullValue()))
        .andExpect(jsonPath("$.offset", notNullValue()))
        .andExpect(jsonPath("$.total", notNullValue()))
        .andExpect(jsonPath("$.results", hasSize(0)));
  }

  @Test
  public void getOrders_userHasNoOrder() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/users/{userId}/orders", 2)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_2);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.limit", notNullValue()))
        .andExpect(jsonPath("$.offset", notNullValue()))
        .andExpect(jsonPath("$.total", notNullValue()))
        .andExpect(jsonPath("$.results", hasSize(0)));
  }

  @Test
  public void getOrders_forbidden_whenPathUserDoesNotMatchToken() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/users/{userId}/orders", 2)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_USER_1);

    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status", equalTo(403)))
        .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")))
        .andExpect(jsonPath("$.message", equalTo("Cannot access another user's orders")));
  }
}
