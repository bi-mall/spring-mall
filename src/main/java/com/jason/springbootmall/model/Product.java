package com.jason.springbootmall.model;

import com.jason.springbootmall.constant.ProductCategory;
import com.jason.springbootmall.constant.ProductStatus;
import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {

  private Integer productId;
  private String productName;
  private ProductCategory category;
  private String imageUrl;
  private Integer price;
  private Integer stock;
  private String description;
  private ProductStatus status;
  private Date createdDate;
  private Date lastModifiedDate;

  public Integer getProductId() {
    return productId;
  }

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public ProductCategory getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = ProductCategory.valueOf(category);
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProductStatus getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = ProductStatus.valueOf(status);
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
}
