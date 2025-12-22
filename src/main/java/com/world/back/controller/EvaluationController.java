package com.world.back.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.back.entity.Evaluation;
import com.world.back.entity.res.Result;
import com.world.back.service.InstituteService;
import com.world.back.utils.PathHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController
{
    @Autowired
    private InstituteService instituteService;
    
    @PostMapping("/save")
    public Result<Boolean> saveEvaluation(@RequestBody Map<String, Object> map) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(PathHelper.root + "/evaluation.json");
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> jsonArray = new ArrayList<>();
        for (String i : map.keySet())
        {
            jsonArray.add(objectMapper.writeValueAsString(map.get(i)));
        }
        fos.write(jsonArray.toString().getBytes());
        fos.close();
        return Result.success(true);
    }
    
    @GetMapping("/load")
    public Result<List<Evaluation>> loadEvaluation() throws IOException
    {
        FileInputStream fis = new FileInputStream(PathHelper.root + "/evaluation.json");
        ObjectMapper objectMapper = new ObjectMapper();
        return Result.success(objectMapper.readValue(fis.readAllBytes(), new TypeReference<List<Evaluation>>(){}));
    }
    
}
