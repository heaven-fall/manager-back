package com.world.back.mapper;

import com.world.back.entity.DateConfig;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface DateConfigMapper {

    @Select("SELECT * FROM date_config WHERE config_key = #{configKey}")
    @Results(id = "dateConfigMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "configKey", column = "config_key"),
            @Result(property = "configValue", column = "config_value"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    DateConfig findByKey(@Param("configKey") String configKey);

    @Select("SELECT * FROM date_config")
    @ResultMap("dateConfigMap")
    List<DateConfig> findAll();

    @Insert({
            "<script>",
            "INSERT INTO date_config (config_key, config_value) ",
            "VALUES (#{configKey}, #{configValue}) ",
            "ON DUPLICATE KEY UPDATE config_value = #{configValue}, updated_at = NOW()",
            "</script>"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int saveOrUpdate(DateConfig dateConfig);

    // 批量保存日期配置
    @Update({
            "<script>",
            "<foreach collection='list' item='item' separator=';'>",
            "INSERT INTO date_config (config_key, config_value) ",
            "VALUES (#{item.configKey}, #{item.configValue}) ",
            "ON DUPLICATE KEY UPDATE config_value = #{item.configValue}, updated_at = NOW()",
            "</foreach>",
            "</script>"
    })
    int batchSaveOrUpdate(@Param("list") List<DateConfig> dateConfigs);
}