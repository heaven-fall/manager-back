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
import java.util.*;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController
{
    @Autowired
    private InstituteService instituteService;
    
    @PostMapping("/save")
    public Result<Boolean> saveEvaluation(@RequestParam String type, @RequestParam Integer year, @RequestBody Map<String, Object> map) throws IOException
    {
        String path;
        if (Objects.equals(type, "1"))
        {
            path = PathHelper.root + "/t" + year + ".json";
        }
        else
        {
            path = PathHelper.root + "/d" + year + ".json";
        }
        FileOutputStream fos = new FileOutputStream(path);
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
    public Result<List<Evaluation>> loadEvaluation(@RequestParam String type, @RequestParam String year) throws IOException
    {
        String path;
        if (Objects.equals(type, "1"))
        {
            path = PathHelper.root + "/t" + year + ".json";
        }
        else
        {
            path = PathHelper.root + "/d" + year + ".json";
        }
        FileInputStream fis = new FileInputStream(path);
        ObjectMapper objectMapper = new ObjectMapper();
        return Result.success(objectMapper.readValue(fis.readAllBytes(), new TypeReference<List<Evaluation>>(){}));
    }
    
}
