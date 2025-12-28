package com.world.back.entity.info;

import com.world.back.entity.TeacherScore;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class DefenseInfo {
    // 学生基础信息
    private String studentId;          // 学号
    private String studentName;        // 学生姓名
    private String phone;              // 电话
    private String email;              // 邮箱

    // 院系信息
    private Integer instituteId;       // 院系ID
    private String instituteName;      // 院系名称

    // 答辩信息
    private Integer defenseGroupId;    // 答辩组号
    private String thesisTitle;        // 题目
    private Integer defenseType;       // 毕业考核类型（0:毕业论文，1:毕业设计）
    private Date defenseTime;          // 答辩日期
    private String summary;            // 摘要

    // 评阅信息
    private String reviewerId;         // 评阅人ID
    private String reviewerName;       // 评阅人姓名

    // 指导老师信息
    private String advisorId;          // 指导教师ID
    private String advisorName;        // 指导教师姓名
    private Integer guidanceYear;      // 指导年份

    // 成绩信息
    private Integer totalScore;        // 总分
    private String comment;            // 答辩小组评语
    private String gradedBy;           // 评分人ID
    private Date gradedAt;             // 评分时间

    // 毕业论文成绩（type=0）
    private Integer paperQuality;      // 论文质量分
    private Integer presentation;      // 自述报告分
    private Integer qaPerformance;     // 回答问题分

    // 毕业设计成绩（type=1）
    private Integer designQuality1;    // 设计质量分1
    private Integer designQuality2;    // 设计质量分2
    private Integer designQuality3;    // 设计质量分3
    private Integer designPresentation; // 设计展示分
    private Integer designQa1;         // 回答问题分1
    private Integer designQa2;         // 回答问题分2

    // 其他教师评分（供答辩组长查看）
    private List<TeacherScore> teacherScores;

    // 状态标志
    private Boolean isScored;          // 是否已评分
    private String scoreStatus;        // 评分状态
}