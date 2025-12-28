package com.world.back.entity;
import lombok.Data;

@Data
public class TeacherScore {
    private String teacherId;      // 教师ID
    private String teacherName;    // 教师姓名
    private Integer totalScore;    // 总分

    // 毕业论文成绩
    private Integer paperQuality;  // 论文质量分
    private Integer presentation;  // 自述报告分
    private Integer qaPerformance; // 回答问题分

    // 毕业设计成绩
    private Integer designQuality1; // 设计质量分1
    private Integer designQuality2; // 设计质量分2
    private Integer designQuality3; // 设计质量分3
    private Integer designPresentation; // 设计展示分
    private Integer designQa1;     // 回答问题分1
    private Integer designQa2;     // 回答问题分2
}