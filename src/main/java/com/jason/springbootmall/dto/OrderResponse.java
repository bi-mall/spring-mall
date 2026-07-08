package com.jason.springbootmall.dto;

import com.jason.springbootmall.model.Order;
import com.jason.springbootmall.model.OrderItem;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OrderResponse {

  private Integer orderId;
  private Integer userId;
  private Integer totalAmount;
  private Date createdDate;
  private Date lastModifiedDate;
  private List<OrderItemResponse> orderItemList;

  public static OrderResponse from(Order order) {
    OrderResponse response = new OrderResponse();
    response.setOrderId(order.getOrderId());
    response.setUserId(order.getUserId());
    response.setTotalAmount(order.getTotalAmount());
    response.setCreatedDate(order.getCreatedDate());
    response.setLastModifiedDate(order.getLastModifiedDate());

    List<OrderItem> orderItemList = order.getOrderItemList();
    if (orderItemList == null) {
      response.setOrderItemList(Collections.emptyList());
    } else {
      response.setOrderItemList(orderItemList.stream().map(OrderItemResponse::from).toList());
    }

    return response;
  }

  public Integer getOrderId() {
    return orderId;
  }

  public void setOrderId(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Integer totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public List<OrderItemResponse> getOrderItemList() {
    return orderItemList;
  }

  public void setOrderItemList(List<OrderItemResponse> orderItemList) {
    this.orderItemList = orderItemList;
  }
}
