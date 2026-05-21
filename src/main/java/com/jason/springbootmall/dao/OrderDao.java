package com.jason.springbootmall.dao;

import com.jason.springbootmall.dto.OrderQueryParams;
import com.jason.springbootmall.model.Order;
import com.jason.springbootmall.model.OrderItem;
import java.util.List;

public interface OrderDao {

  Integer countOrder(OrderQueryParams orderQueryParams);

  List<Order> getOrders(OrderQueryParams orderQueryParams);

  Order getOrderById(Integer orderId);

  List<OrderItem> getOrderItemsByOrderId(Integer orderId);

  List<OrderItem> getOrderItemsByOrderIds(List<Integer> orderIds);

  Integer createOrder(Integer userId, Integer totalAmount);

  void createOrderItem(Integer orderId, List<OrderItem> orderItemList);
}
