package com.world.back.utils;

import com.world.back.entity.user.DefenseLeader;
import com.world.back.entity.user.Teacher;

public class EntityHelper {

    // 构建答辩组长对象（带年份）
    public static DefenseLeader buildDefenseLeader(Teacher teacher, Integer year) {
        DefenseLeader leader = new DefenseLeader();

        // 复制Teacher属性
        leader.setId(teacher.getId());
        leader.setPwd(teacher.getPwd());
        leader.setRole(teacher.getRole());
        leader.setGroupYear(year);
        leader.setRealName(teacher.getRealName());
        leader.setInstId(teacher.getInstId());
        leader.setInstituteName(teacher.getInstituteName());
        leader.setIsDefenseLeader(true);
        leader.setGroupId(teacher.getGroupId());
        leader.setGuidedStudentsCount(teacher.getGuidedStudentsCount());

        return leader;
    }
}
