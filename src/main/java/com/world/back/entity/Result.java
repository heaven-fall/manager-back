package com.world.back.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 基础响应类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
  private Integer code;
  private String message;
  private T data;

  // 成功响应
  public static <T> Result<T> success(T data) {
    return new Result<>(200, "成功", data);
  }

  public static <T> Result<T> success() {
    return new Result<>(200, "成功",null);
  }

  public static <T> Result<T> success(String message, T data) {
    return new Result<>(200, message, data);
  }

  // 失败响应
  public static <T> Result<T> error(String message) {
    return new Result<>(500, message, null);
  }

  public static <T> Result<T> error(Integer code, String message) {
    return new Result<>(code, message, null);
  }

  // 便捷方法
  public boolean isSuccess() {
    return code != null && code == 200;
  }
}