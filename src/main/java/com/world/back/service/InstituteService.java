package com.world.back.service;

import com.world.back.entity.Institute;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InstituteService
{
  Long getInstituteCount();
  List<Institute> getAll();
  String getInstituteNameById(Integer id);
  Boolean updateInstitute(Institute institute);
  Boolean deleteInstitute(Integer id);
  void addInstitute(Institute institute);
}
