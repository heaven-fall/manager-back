package com.world.back.entity;

import com.world.back.entity.user.BaseUser;
import lombok.Data;

@Data
public class Institute {
    private Integer id;
    private String name;
    private String adminId;

    private Integer teacherCount;    // 教师数量
    private Integer studentCount;    // 学生数量
    private BaseUser adminUser;

    public Institute(int id, String name) {
        this.id=id;
        this.name=name;
    }

}