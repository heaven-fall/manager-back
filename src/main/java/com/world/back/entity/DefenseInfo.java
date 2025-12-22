package com.world.back.entity;

import lombok.Data;
import java.util.Date;

@Data
public class DefenseInfo {
    private Integer groupId;         // 答辩组号
    private String studentId;        // 学生编号
    private Integer type;            // 毕业考核类型：1-论文，2-设计
    private String title;            // 毕业考核题目
    private Date time;               // 答辩日期

    private Student student;

    private DefenseGroup defenseGroup;
}