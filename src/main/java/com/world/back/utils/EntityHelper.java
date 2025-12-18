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
        leader.setRealName(teacher.getRealName());
        leader.setInstituteId(teacher.getInstituteId());
        leader.setInstituteName(teacher.getInstituteName());
        leader.setIsDefenseLeader(true);
        leader.setGuidedStudentsCount(teacher.getGuidedStudentsCount());

        // 设置答辩组长特有属性
        leader.setYear(year);

        // 这里可以查询具体的答辩组信息
        // DefenseGroup group = defenseGroupMapper.findByLeaderIdAndYear(teacher.getId(), year);
        // if (group != null) {
        //     leader.setGroupId(group.getId());
        //     leader.setGroupName(group.getGroupName());
        // }

        return leader;
    }

    // 简化的构建方法（不带年份）
    public static DefenseLeader buildDefenseLeader(Teacher teacher) {
        return buildDefenseLeader(teacher, null);
    }
}