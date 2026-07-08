package com.jason.springbootmall.dto;

import com.jason.springbootmall.model.OrderItem;

public class OrderItemResponse {

  private Integer orderItemId;
  private Integer productId;
  private Integer quantity;
  private Integer amount;
  private String productName;
  private String imageUrl;

  public static OrderItemResponse from(OrderItem orderItem) {
    OrderItemResponse response = new OrderItemResponse();
    response.setOrderItemId(orderItem.getOrderItemId());
    response.setProductId(orderItem.getProductId());
    response.setQuantity(orderItem.getQuantity());
    response.setAmount(orderItem.getAmount());
    response.setProductName(orderItem.getProductName());
    response.setImageUrl(orderItem.getImageUrl());
    return response;
  }

  public Integer getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(Integer orderItemId) {
    this.orderItemId = orderItemId;
  }

  public Integer getProductId() {
    return productId;
  }

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
