package com.world.back.entity.info;

import lombok.Data;

@Data
public class AdvisorInfo {
    private String studentId;      // 学生ID
    private String advisorId;      // 指导教师ID
    private String advisorName;    // 指导教师姓名
    private Integer guidanceYear;  // 指导年份
}
