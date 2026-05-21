package com.jason.springbootmall.controller;

import com.jason.springbootmall.constant.ProductCategory;
import com.jason.springbootmall.constant.ProductSortField;
import com.jason.springbootmall.constant.SortDirection;
import com.jason.springbootmall.dto.ProductQueryParams;
import com.jason.springbootmall.dto.ProductRequest;
import com.jason.springbootmall.model.Product;
import com.jason.springbootmall.service.ProductService;
import com.jason.springbootmall.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Valid
@RestController
public class ProductController {

  @Autowired private ProductService productService;

  @GetMapping("/products")
  public ResponseEntity<Page<Product>> getProducts(
      // Filtering
      @RequestParam(required = false) ProductCategory category,
      @RequestParam(required = false) String search,
      // Sorting
      @RequestParam(defaultValue = "createdDate") String orderBy,
      @RequestParam(defaultValue = "desc") String sort,
      // Pagination
      @RequestParam(defaultValue = "5") @Max(1000) @Min(1) Integer limit,
      @RequestParam(defaultValue = "0") @Min(0) Integer offset) {
    ProductSortField sortField = parseSortField(orderBy);
    SortDirection sortDirection = parseSortDirection(sort);

    ProductQueryParams productQueryParams = new ProductQueryParams();
    productQueryParams.setSearch(search);
    productQueryParams.setCategory(category);
    productQueryParams.setOrderBy(sortField);
    productQueryParams.setSort(sortDirection);
    productQueryParams.setLimit(limit);
    productQueryParams.setOffset(offset);
    // 取得 product list
    List<Product> productList = productService.getProducts(productQueryParams);
    // 取得總數
    Integer total = productService.countProduct(productQueryParams);
    // 分頁
    Page<Product> page = new Page<>();
    page.setLimit(limit);
    page.setOffset(offset);
    page.setTotal(total);
    page.setResults(productList);

    return ResponseEntity.status(HttpStatus.OK).body(page);
  }

  @GetMapping("/products/{productId}")
  public ResponseEntity<Product> getProduct(@PathVariable Integer productId) {
    Product product = productService.getProductById(productId);

    if (product != null) {
      return ResponseEntity.status(HttpStatus.OK).body(product);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @PostMapping("/products")
  public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {
    Integer productId = productService.createProduct(productRequest);

    Product product = productService.getProductById(productId);

    return ResponseEntity.status(HttpStatus.CREATED).body(product);
  }

  @PutMapping("/products/{productId}")
  public ResponseEntity<Product> updateProduct(
      @PathVariable Integer productId, @RequestBody @Valid ProductRequest productRequest) {
    // 檢查product 是否存在
    Product product = productService.getProductById(productId);
    if (product == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    // 修改商品的數據
    productService.updateProduct(productId, productRequest);

    Product updatedProduct = productService.getProductById(productId);

    return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
  }

  @DeleteMapping("/products/{productId}")
  public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
    boolean deleted = productService.deleteProductById(productId);
    if (!deleted) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/test")
  public String test() {
    return "vercel!";
  }

  private ProductSortField parseSortField(String orderBy) {
    return ProductSortField.from(orderBy)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid orderBy parameter"));
  }

  private SortDirection parseSortDirection(String sort) {
    return SortDirection.from(sort)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort parameter"));
  }
}
