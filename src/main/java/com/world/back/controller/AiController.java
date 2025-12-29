package com.world.back.controller;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.world.back.entity.res.Result;
import com.world.back.mapper.StudentMapper;
import com.world.back.utils.PathHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController
{
    @Autowired
    StudentMapper studentMapper;
    
    @PostMapping("/config/save")
    public Result<Boolean> saveConfig(@RequestBody Map<String, Object> map) throws IOException
    {
        String aiconfig = PathHelper.root + "/config/ai.json";
        FileOutputStream fos = new FileOutputStream(aiconfig);
        ObjectMapper objectMapper = new ObjectMapper();
        fos.write(objectMapper.writeValueAsBytes(map));
        fos.close();
        return Result.success(true);
    }
    
    @GetMapping("/config/load")
    public Result<Map<String, Object>> loadConfig() throws IOException
    {
        String aiconfig = PathHelper.root + "/config/ai.json";
        FileInputStream fis = new FileInputStream(aiconfig);
        ObjectMapper objectMapper = new ObjectMapper();
        return Result.success(objectMapper.readValue(fis.readAllBytes(), Map.class));
    }
    
    @PostMapping("/prompts/save")
    public Result<Boolean> savePrompt(@RequestBody Map<String, Object> map) throws IOException
    {
        String type = map.get("type").toString();
        String aiconfig = PathHelper.root + "/config/" + type + "prompt.json";
        FileOutputStream fos = new FileOutputStream(aiconfig);
        fos.write(map.get("prompt").toString().getBytes());
        fos.close();
        return Result.success(true);
    }
    
    @GetMapping("/prompts/load")
    public Result<Map<String, Object>> loadPrompt() throws IOException
    {
        String aiconfig = PathHelper.root + "/config/thesisprompt.json";
        BufferedReader br1 = new BufferedReader(new FileReader(aiconfig));
        Map<String, Object> res = new HashMap<>();
        String temp1 = "";
        String temp2 = "";
        while ((temp1 = br1.readLine()) != null)
        {
            temp2 += temp1 + "\n";
        }
        res.put("thesisCommentPrompt", temp2);
        br1.close();
        aiconfig = PathHelper.root + "/config/designprompt.json";
        BufferedReader br2 = new BufferedReader(new FileReader(aiconfig));
        temp1 = "";
        temp2 = "";
        while ((temp1 = br2.readLine()) != null)
        {
            temp2 += temp1 + "\n";
        }
        res.put("designCommentPrompt", temp2);
        return Result.success(res);
    }
    
    @PostMapping("/system-config/save")
    public Result<Boolean> saveSystemConfig(@RequestBody Map<String, Object> map) throws IOException
    {
        String aiconfig = PathHelper.root + "/config/systemconfig.json";
        FileOutputStream fos = new FileOutputStream(aiconfig);
        ObjectMapper objectMapper = new ObjectMapper();
        fos.write(objectMapper.writeValueAsBytes(map));
        fos.close();
        return Result.success(true);
    }
    
    @GetMapping("/system-config/load")
    public Result<Map<String, Object>> loadSystemConfig() throws IOException
    {
        String aiconfig = PathHelper.root + "/config/systemconfig.json";
        FileInputStream fis = new FileInputStream(aiconfig);
        ObjectMapper objectMapper = new ObjectMapper();
        return Result.success(objectMapper.readValue(fis.readAllBytes(), Map.class));
    }
    
    @PostMapping("/comment/generate")
    public Result<String> generateComment(@RequestBody Map<String, Object> map) throws IOException, NoApiKeyException, InputRequiredException
    {
        String prompt = map.get("prompt").toString();
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(prompt)
                .build();
        
        String aiconfig = PathHelper.root + "/config/ai.json";
        FileInputStream fis = new FileInputStream(aiconfig);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> config = objectMapper.readValue(fis.readAllBytes(), Map.class);
        GenerationParam param = GenerationParam.builder()
                .apiKey(config.get("apiKey").toString())
                .model(config.get("modelName").toString())
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return Result.success(JsonUtils.toJson(gen.call(param)));
    }
    
    @PostMapping("/comment/save")
    public Result<Boolean> saveComment(@RequestBody Map<String, Object> map) throws IOException
    {
        Integer group_id = (Integer) map.get("gid");
        String student_id = (String) map.get("student_id");
        String comment = (String)map.get("comment");
        studentMapper.saveComment(group_id, student_id, comment);
        return Result.success(true);
    }
    
}
