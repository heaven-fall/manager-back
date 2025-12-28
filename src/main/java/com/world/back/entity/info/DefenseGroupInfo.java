package com.world.back.entity.info;

import lombok.Data;

@Data
public class DefenseGroupInfo {
    // 小组基础信息
    private Integer groupId;             // 小组ID
    private Integer year;                // 答辩年份
    private String adminName;             // 答辩组长name
    private Integer maxStudentCount;    // 最大学生数量

    // 教师在该小组中的角色
    private Boolean isDefenseLeader;
    private String teacherId;

    // 统计信息
    private Integer totalStudentCount;   // 小组成员总数
    private Integer scoredStudentCount;  // 已评分学生数
}