package com.world.back.entity;

import lombok.Data;

@Data
public class Coefficient {
    private Integer groupId;
    private String stuId;
    private int majorScore;
    private double adjustmentCoefficient;
}
