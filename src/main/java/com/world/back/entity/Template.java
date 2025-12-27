package com.world.back.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@Data
@TableName("template")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Template {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private Integer type;
    private String filePath;
    private String fileName;
    private Long fileSize;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    // 以下是需要添加的字段
    @TableField(exist = false)
    private Boolean hasTemplate;

    @TableField(exist = false)
    private String[] requiredPlaceholders;

    @TableField(exist = false)
    private String updatedAtFormatted;

    @TableField(exist = false)
    private String updatedBy;

    // 数据库字段（需要在数据库表中添加）
    @TableField(value = "updated_by")
    private String dbUpdatedBy;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}