package com.world.back.serviceImpl;

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
        groupMapper.createGroup(group.getAdmin_id(), group.getYear(), group.getMax_student_count());
    }
}
