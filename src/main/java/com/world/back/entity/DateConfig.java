package com.world.back.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("date_config")
public class DateConfig {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String configKey;
    private Date configValue;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
