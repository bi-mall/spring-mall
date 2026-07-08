package com.jason.springbootmall.service;

import com.jason.springbootmall.dto.CreateOrderRequest;
import com.jason.springbootmall.dto.OrderQueryParams;
import com.jason.springbootmall.dto.OrderResponse;
import java.util.List;

public interface OrderService {

  Integer countOrder(OrderQueryParams orderQueryParams);

  List<OrderResponse> getOrders(OrderQueryParams orderQueryParams);

  Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

  OrderResponse getOrderById(Integer orderId);
}
