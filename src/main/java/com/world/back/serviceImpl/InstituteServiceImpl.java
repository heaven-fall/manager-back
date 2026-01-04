package com.world.back.serviceImpl;

import com.world.back.entity.Institute;
import com.world.back.mapper.InstituteMapper;
import com.world.back.mapper.UserMapper;
import com.world.back.service.InstituteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
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

      // 查询该院系的管理员姓名
      if (institute.getAdminId() != null && !institute.getAdminId().isEmpty()) {
        String adminName = userMapper.getRealNameById(institute.getAdminId());
        institute.setAdminName(adminName != null ? adminName : "未知管理员");
      }
    }
    return list;
  }

  @Override
  public String getInstituteNameById(Integer id)
  {
    return instituteMapper.getInstituteNameById(id);
  }

  @Override
  public Boolean updateInstitute(Institute institute) {
    try {
      return instituteMapper.updateInstitute(institute);
    } catch (Exception e) {
      log.error("更新院系失败", e);
      return false;
    }
  }

  @Override
  public Boolean deleteInstitute(Integer id)
  {
    try {
      instituteMapper.deleteInstitute(id);
      return true;
    } catch (Exception e) {
      log.error("删除院系失败", e);
      return false;
    }
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

    // 插入院系 - adminId可以为null
    instituteMapper.addInstitute(institute.getName(), institute.getAdminId());
  }

  // 获取管理员管理的所有院系
  public List<Institute> getInstitutesByAdminId(String adminId) {
    return instituteMapper.getInstitutesByAdminId(adminId);
  }

  // 获取未安排管理员的院系
  public List<Institute> getAvailableInstitutes() {
    return instituteMapper.getAvailableInstitutes();
  }

  // 添加检查院系名称是否已存在的方法
  private boolean checkInstituteNameExists(String name) {
    return instituteMapper.checkInstituteNameExists(name) > 0;
  }
}