package com.world.back.entity.res;


import lombok.Data;

@Data
public class LoginWithYearRequest {
    private String username;
    private String password;
    private Integer year;
    private Integer group_id;
}

