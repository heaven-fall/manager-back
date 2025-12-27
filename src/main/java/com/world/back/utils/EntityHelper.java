package com.world.back.utils;

import com.world.back.entity.user.DefenseLeader;
import com.world.back.entity.user.Teacher;

public class EntityHelper {

    // 构建答辩组长对象（带年份）
    public static DefenseLeader buildDefenseLeader(Teacher teacher, Integer year) {
        DefenseLeader defenseLeader = new DefenseLeader();

        defenseLeader.setId(teacher.getId());
        defenseLeader.setPwd(teacher.getPwd());
        defenseLeader.setRole(teacher.getRole());
        defenseLeader.setRealName(teacher.getRealName());
        defenseLeader.setInstituteId(teacher.getInstituteId());
        defenseLeader.setInstituteName(teacher.getInstituteName());
        defenseLeader.setIsDefenseLeader(true);
        defenseLeader.setGroupId(teacher.getGroupId());

        return defenseLeader;
    }

}