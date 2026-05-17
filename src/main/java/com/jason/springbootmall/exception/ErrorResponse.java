package com.jason.springbootmall.exception;

import java.util.List;

public class ErrorResponse {

  // Keep every API error in the same JSON shape so frontend code can handle it consistently.
  private Integer status;
  private String code;
  private String message;
  private String path;
  private List<FieldErrorDetail> errors;

  public ErrorResponse(
      Integer status, String code, String message, String path, List<FieldErrorDetail> errors) {
    this.status = status;
    this.code = code;
    this.message = message;
    this.path = path;
    this.errors = errors;
  }

  public static ErrorResponse of(Integer status, String code, String message, String path) {
    return new ErrorResponse(status, code, message, path, List.of());
  }

  public static ErrorResponse withErrors(
      Integer status, String code, String message, String path, List<FieldErrorDetail> errors) {
    return new ErrorResponse(status, code, message, path, errors);
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<FieldErrorDetail> getErrors() {
    return errors;
  }

  public void setErrors(List<FieldErrorDetail> errors) {
    this.errors = errors;
  }

  public static class FieldErrorDetail {
    private String field;
    private String message;

    public FieldErrorDetail(String field, String message) {
      this.field = field;
      this.message = message;
    }

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
}
