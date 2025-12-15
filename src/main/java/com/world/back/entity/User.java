package com.world.back.entity;

import lombok.Data;

@Data
public class User
{
  private String username;
  private String password;
  int role;
}
