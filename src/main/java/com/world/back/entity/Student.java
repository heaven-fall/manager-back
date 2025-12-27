package com.world.back.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Student {
    private String id;
    private String realName;
    private String tel;
    private String email;
    private Integer instituteId;
    private String instituteName;

    // 毕业考核信息
    private Integer type;    // 考核类型：1-论文，2-设计
    private String defenseTitle;    // 考核题目
    private String defenseDate;     // 答辩日期

    // 指导关系
    private String advisorId;       // 指导教师ID
    private String advisorName;     // 指导教师姓名

    // 答辩组信息
    private Integer defenseGroupId; // 答辩组ID
    private String defenseGroupName; // 答辩组名称
    private String defenseGroupLeaderId; // 答辩组长ID
    private String defenseGroupLeaderName; // 答辩组长姓名
}
