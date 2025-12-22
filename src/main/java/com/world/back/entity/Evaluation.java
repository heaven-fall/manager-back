package com.world.back.entity;

import lombok.Data;

@Data
public class Evaluation
{
    private Integer id;
    private String name;
    private String content;
    private String criteria;
    private Integer weight;
}
