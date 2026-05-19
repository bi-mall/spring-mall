package com.jason.springbootmall.rowmapper;

import com.jason.springbootmall.model.Product;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProductRowMapper implements RowMapper<Product> {

  @Override
  public Product mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Product product = new Product();

    product.setProductId(resultSet.getInt("product_id"));
    product.setProductName(resultSet.getString("product_name"));
    product.setCategory(String.valueOf(resultSet.getString("category")));
    product.setImageUrl(resultSet.getString("image_url"));
    product.setPrice(resultSet.getInt("price"));
    product.setStock(resultSet.getInt("stock"));
    product.setDescription(resultSet.getString("description"));
    product.setStatus(resultSet.getString("status"));
    product.setCreatedDate(resultSet.getTimestamp("created_date"));
    product.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));

    return product;
  }
}
