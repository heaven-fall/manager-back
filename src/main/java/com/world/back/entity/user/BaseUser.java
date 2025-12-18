package com.world.back.entity.user;

import lombok.Data;

@Data
public class BaseUser {
    protected String id;
    protected String realName;
    protected String pwd;
    protected Integer role;
}