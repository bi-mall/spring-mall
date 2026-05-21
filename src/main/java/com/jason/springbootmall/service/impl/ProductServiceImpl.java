package com.jason.springbootmall.service.impl;

import com.jason.springbootmall.dao.ProductDao;
import com.jason.springbootmall.dto.ProductQueryParams;
import com.jason.springbootmall.dto.ProductRequest;
import com.jason.springbootmall.model.Product;
import com.jason.springbootmall.service.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceImpl implements ProductService {

  @Autowired private ProductDao productDao;

  @Override
  public Integer countProduct(ProductQueryParams productQueryParams) {
    return productDao.countProduct(productQueryParams);
  }

  @Override
  public List<Product> getProducts(ProductQueryParams productQueryParams) {
    return productDao.getProducts(productQueryParams);
  }

  @Override
  @Cacheable(value = "product", key = "#productId")
  public Product getProductById(Integer productId) {
    return productDao.getProductById(productId);
  }

  @Override
  public Integer createProduct(ProductRequest productRequest) {
    return productDao.createProduct(productRequest);
  }

  @Override
  @CacheEvict(value = "product", key = "#productId")
  public void updateProduct(Integer productId, ProductRequest productRequest) {
    productDao.updateProduct(productId, productRequest);
  }

  @Override
  @CacheEvict(value = "product", key = "#productId")
  public boolean deleteProductById(Integer productId) {
    return productDao.deleteProductById(productId);
  }
}
