package com.world.back.serviceImpl;

import com.world.back.entity.Institute;
import com.world.back.mapper.InstituteMapper;
import com.world.back.mapper.UserMapper;
import com.world.back.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstituteServiceImpl implements InstituteService
{
  @Autowired
  private InstituteMapper instituteMapper;
  @Autowired
  private UserMapper userMapper;
  @Override
  public Long getInstituteCount()
  {
    return instituteMapper.getInstituteCount();
  }

  @Override
  public List<Institute> getAll()
  {
    List<Institute> list = instituteMapper.getAll();
    for (Institute institute : list)
    {
      institute.setTeacherCount(instituteMapper.getTeacherCount(institute.getId()));
      institute.setStudentCount(instituteMapper.getStudentCount(institute.getId()));
      institute.setAdminName(userMapper.getRealNameById(institute.getAdminId()));
    }
    return list;
  }

  @Override
  public String getInstituteNameById(Integer id)
  {
    return instituteMapper.getInstituteNameById(id);
  }

  @Override
  public Boolean updateInstitute(Institute institute)
  {
    String user_id = institute.getAdminId();
    if (Objects.equals(user_id, ""))
    {
      user_id = "admin";
    }
    institute.setAdminId(user_id);
    instituteMapper.updateInstitute(institute.getId(), institute.getName(), institute.getAdminId());
    userMapper.deleteUserInstRel(institute.getId());
    userMapper.createUserInstRel(institute.getAdminId(), institute.getId());
    return true;
  }

  @Override
  public Boolean deleteInstitute(Integer id)
  {
    instituteMapper.deleteInstitute(id);
    return true;
  }

    @Override
    public void addInstitute(Institute institute)
    {
      // 验证院系名称是否为空
      if (institute.getName() == null || institute.getName().trim().isEmpty()) {
        throw new RuntimeException("院系名称不能为空");
      }

      // 检查院系名称是否已存在
      if (checkInstituteNameExists(institute.getName())) {
        throw new RuntimeException("院系名称 '" + institute.getName() + "' 已存在，请使用其他名称");
      }

      instituteMapper.addInstitute(institute.getName(), institute.getAdminId());
      userMapper.createUserInstRel(institute.getAdminId(), institute.getId());
    }

    // 添加检查院系名称是否已存在的方法
    private boolean checkInstituteNameExists(String name) {
      // 这里需要添加一个方法来检查院系名称是否已存在
      // 您可以修改InstituteMapper来添加这个方法
      return instituteMapper.checkInstituteNameExists(name) > 0;
    }





}
