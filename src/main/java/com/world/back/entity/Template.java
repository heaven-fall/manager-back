// Template.java - 修正版
package com.world.back.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Template {
    private Integer id;
    private String name;
    private Integer type;
    private String description;
    private String filePath;
    private String fileName;
    private Long fileSize;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    private String updatedBy;

    // 新增：数据库查询返回的占位符字符串
    private String placeholderKeys;

    // 非数据库字段
    private Boolean hasTemplate;

    // 注意：这里不需要 requiredPlaceholders 字段，因为前端会处理
    // private List<String> requiredPlaceholders;
}