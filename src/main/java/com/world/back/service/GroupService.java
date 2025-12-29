package com.world.back.service;

import com.world.back.entity.Student;
import com.world.back.entity.res.Group;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface GroupService
{
    List<Map<String, Object>> getAllGroups(Integer year);

    Boolean createGroup(Group group);

    void updateGroup(Group group);

    void deleteGroup(Integer id);

    void deleteAdmin(Integer gid);

    int getMaxStudentCountByGid(Integer group_id);
    
    List<Map<String, Object>> getMember(Integer group_id);
    
    void deleteFromGroup(Integer group_id, String student_id);
}
