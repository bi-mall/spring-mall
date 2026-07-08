package com.jason.springbootmall.service.impl;

import com.jason.springbootmall.dao.OrderDao;
import com.jason.springbootmall.dao.ProductDao;
import com.jason.springbootmall.dao.UserDao;
import com.jason.springbootmall.dto.BuyItem;
import com.jason.springbootmall.dto.CreateOrderRequest;
import com.jason.springbootmall.dto.OrderQueryParams;
import com.jason.springbootmall.dto.OrderResponse;
import com.jason.springbootmall.model.Order;
import com.jason.springbootmall.model.OrderItem;
import com.jason.springbootmall.model.Product;
import com.jason.springbootmall.model.User;
import com.jason.springbootmall.service.OrderService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OrderServiceImpl implements OrderService {

  private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
  @Autowired private OrderDao orderDao;
  @Autowired private ProductDao productDao;
  @Autowired private UserDao userDao;

  @Override
  public Integer countOrder(OrderQueryParams orderQueryParams) {
    return orderDao.countOrder(orderQueryParams);
  }

  @Override
  public List<OrderResponse> getOrders(OrderQueryParams orderQueryParams) {
    List<Order> orderList = orderDao.getOrders(orderQueryParams);

    if (orderList.isEmpty()) {
      return Collections.emptyList();
    }

    List<Integer> orderIds = orderList.stream().map(Order::getOrderId).toList();
    List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderIds(orderIds);
    Map<Integer, List<OrderItem>> orderItemsByOrderId =
        orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));

    for (Order order : orderList) {
      order.setOrderItemList(
          orderItemsByOrderId.getOrDefault(order.getOrderId(), Collections.emptyList()));
    }

    return orderList.stream().map(OrderResponse::from).toList();
  }

  // Roll back stock, order, and order_item changes together if any order step fails.
  @Transactional
  @Override
  public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
    validateUserExists(userId);

    int totalAmount = 0;
    List<OrderItem> orderItemList = new ArrayList<>();

    for (BuyItem buyItem : createOrderRequest.getBuyItemList()) {
      Product product = getProductOrThrow(buyItem.getProductId());
      validateStockEnough(product, buyItem);
      decreaseStockOrThrow(product, buyItem);

      int amount = calculateAmount(product, buyItem);
      totalAmount = totalAmount + amount;

      orderItemList.add(buildOrderItem(buyItem, amount));
    }

    Integer orderId = orderDao.createOrder(userId, totalAmount);

    orderDao.createOrderItem(orderId, orderItemList);

    return orderId;
  }

  @Override
  public OrderResponse getOrderById(Integer orderId) {
    Order order = orderDao.getOrderById(orderId);

    if (order == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order does not exist");
    }

    List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);

    order.setOrderItemList(orderItemList);

    return OrderResponse.from(order);
  }

  private void validateUserExists(Integer userId) {
    User user = userDao.getUserById(userId);

    if (user == null) {
      log.warn("User does not exist, userId={}", userId);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
    }
  }

  private Product getProductOrThrow(Integer productId) {
    Product product = productDao.getProductById(productId);

    if (product == null) {
      log.warn("Product does not exist, productId={}", productId);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product does not exist");
    }

    return product;
  }

  private void validateStockEnough(Product product, BuyItem buyItem) {
    if (product.getStock() < buyItem.getQuantity()) {
      log.warn(
          "Product stock is not enough, productId={}, stock={}, requestedQuantity={}",
          buyItem.getProductId(),
          product.getStock(),
          buyItem.getQuantity());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product stock is not enough");
    }
  }

  private void decreaseStockOrThrow(Product product, BuyItem buyItem) {
    boolean stockUpdated = productDao.decreaseStock(product.getProductId(), buyItem.getQuantity());

    if (!stockUpdated) {
      log.warn(
          "Product stock changed before update, productId={}, requestedQuantity={}",
          buyItem.getProductId(),
          buyItem.getQuantity());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product stock is not enough");
    }
  }

  private int calculateAmount(Product product, BuyItem buyItem) {
    return buyItem.getQuantity() * product.getPrice();
  }

  private OrderItem buildOrderItem(BuyItem buyItem, int amount) {
    OrderItem orderItem = new OrderItem();
    orderItem.setProductId(buyItem.getProductId());
    orderItem.setQuantity(buyItem.getQuantity());
    orderItem.setAmount(amount);

    return orderItem;
  }
}
