package com.world.back.serviceImpl;

import com.world.back.entity.Student;
import com.world.back.entity.res.Group;
import com.world.back.mapper.GroupMapper;
import com.world.back.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GroupServiceImpl implements GroupService
{
    @Autowired
    private GroupMapper groupMapper;
    @Override
    public List<Map<String, Object>> getAllGroups(Integer year)
    {
        return groupMapper.getAllGroups(year);
    }

    @Override
    public void createGroup(Group group)
    {
        groupMapper.createGroup(group);
    }

    @Override
    public void updateGroup(Group group)
    {
        groupMapper.updateGroup(group.getId(), group.getAdmin_id(), group.getMax_student_count());
    }

    @Override
    public void deleteGroup(Integer id)
    {
        groupMapper.beforeDeleteGroup(id);
        groupMapper.deleteGroup(id);
    }

    @Override
    public void deleteAdmin(Integer gid)
    {
        groupMapper.deleteAdmin(gid);
    }

    @Override
    public List<Student> getStudentByGid(Integer group_id)
    {
        return groupMapper.getStudentByGid(group_id);
    }

    @Override
    public int getMaxStudentCountByGid(Integer group_id)
    {
        return groupMapper.getMaxStudentCountByGid(group_id);
    }
}
