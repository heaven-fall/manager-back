package com.world.back.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Institute {
    private Integer id;
    private String name;
    private String adminId;  // 管理员ID（一个院系对应一个管理员）

    // 以下是计算字段，不存储在数据库
    private String adminName;  // 管理员姓名
    private Integer teacherCount;    // 教师数量
    private Integer studentCount;    // 学生数量

    // 无参构造函数
    public Institute() {
    }

    // 用于添加的构造函数
    public Institute(String name, String adminId) {
        this.name = name;
        this.adminId = adminId;
    }

    // 无管理员的构造函数
    public Institute(String name) {
        this.name = name;
    }
}