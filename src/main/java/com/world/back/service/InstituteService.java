package com.world.back.service;

import com.world.back.entity.Institute;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InstituteService
{
  Long getInstituteCount();
  List<Institute> getAll();
  void addInstitute(Institute institute);
}
