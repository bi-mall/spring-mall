package com.jason.springbootmall.controller;

import com.jason.springbootmall.dto.CreateOrderRequest;
import com.jason.springbootmall.dto.OrderQueryParams;
import com.jason.springbootmall.dto.OrderResponse;
import com.jason.springbootmall.model.User;
import com.jason.springbootmall.service.OrderService;
import com.jason.springbootmall.service.UserService;
import com.jason.springbootmall.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class OrderController {

  @Autowired private OrderService orderService;
  @Autowired private UserService userService;

  @GetMapping("/users/{userId}/orders")
  public ResponseEntity<Page<OrderResponse>> getOrders(
      @PathVariable Integer userId,
      @RequestParam(defaultValue = "10") @Max(1000) @Min(1) Integer limit,
      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
      Authentication authentication) {
    validateOwner(userId, authentication);

    OrderQueryParams orderQueryParams = new OrderQueryParams();
    orderQueryParams.setUserId(userId);
    orderQueryParams.setOffset(offset);
    orderQueryParams.setLimit(limit);

    List<OrderResponse> orderList = orderService.getOrders(orderQueryParams);
    Integer count = orderService.countOrder(orderQueryParams);

    Page<OrderResponse> page = new Page<>();
    page.setLimit(limit);
    page.setOffset(offset);
    page.setTotal(count);
    page.setResults(orderList);

    return ResponseEntity.status(HttpStatus.OK).body(page);
  }

  @PostMapping("/users/{userId}/orders")
  public ResponseEntity<OrderResponse> createOrder(
      @PathVariable Integer userId,
      @RequestBody @Valid CreateOrderRequest createOrderRequest,
      Authentication authentication) {
    validateOwner(userId, authentication);

    Integer orderId = orderService.createOrder(userId, createOrderRequest);

    OrderResponse order = orderService.getOrderById(orderId);

    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }

  private void validateOwner(Integer userId, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
    }

    User currentUser = userService.getUserByEmail(authentication.getName());

    if (currentUser == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
    }

    if (!currentUser.getUserId().equals(userId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Cannot access another user's orders");
    }
  }
}
