package com.world.back.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DefenseService
{
    void yearAdd(Integer year);
    
    List<Map<String, Object>> yearAll();
}
