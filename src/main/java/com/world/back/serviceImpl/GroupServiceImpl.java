package com.world.back.serviceImpl;

import com.world.back.entity.Student;
import com.world.back.entity.res.Group;
import com.world.back.mapper.GroupMapper;
import com.world.back.mapper.StudentMapper;
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
    @Autowired
    private StudentMapper studentMapper;
    @Override
    public List<Map<String, Object>> getAllGroups(Integer year)
    {
        return groupMapper.getAllGroups(year);
    }

    @Override
    public Boolean createGroup(Group group) throws RuntimeException
    {
        if (groupMapper.getGidByYearId(group.getYear(), group.getAdmin_id()) != null)
        {
            throw new RuntimeException("该老师在该年份已成为组长");
        }
        groupMapper.createGroup(group);
        return true;
    }

    @Override
    public void updateGroup(Group group)
    {
        groupMapper.updateGroup(group.getId(), group.getAdmin_id(), group.getMax_student_count());
    }

    @Override
    public void deleteGroup(Integer id)
    {
        groupMapper.deleteGroupInfo(id);
        groupMapper.deleteGroupRelation(id);
        groupMapper.deleteGroup(id);
    }

    @Override
    public void deleteAdmin(Integer gid)
    {
        groupMapper.deleteAdmin(gid);
    }
    

    @Override
    public int getMaxStudentCountByGid(Integer group_id)
    {
        return groupMapper.getMaxStudentCountByGid(group_id);
    }
    
    @Override
    public List<Map<String, Object>> getMember(Integer group_id)
    {
        List<Map<String, Object>> groupMembers = groupMapper.getMember(group_id);
        for (Map<String, Object> map : groupMembers) {
            Student student = studentMapper.findById((String)map.get("stu_id"));
            Map<String, Object> dbinfo = studentMapper.getDbInfoById(student.getId());
            map.put("realName", student.getRealName());
            map.put("title", dbinfo.get("title"));
            map.put("instituteId", student.getInstituteId());
            map.put("summary", dbinfo.get("summary"));
            map.put("type", dbinfo.get("type"));
        }
        return groupMembers;
    }
    
    @Override
    public void deleteFromGroup(Integer group_id, String student_id)
    {
        groupMapper.deleteFromGroup(group_id, student_id);
    }
}
