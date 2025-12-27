package com.world.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlaceholderConfigMapper {

    @Select("SELECT placeholder_key FROM placeholder_config " +
            "WHERE template_type = #{templateType} AND is_required = 1 " +
            "ORDER BY id")
    List<String> findRequiredPlaceholdersByType(@Param("templateType") Integer templateType);

    @Select("SELECT COUNT(*) FROM placeholder_config " +
            "WHERE template_type = #{templateType} AND placeholder_key = #{placeholderKey}")
    int checkPlaceholderExists(@Param("templateType") Integer templateType,
                               @Param("placeholderKey") String placeholderKey);

    @Select("SELECT COUNT(*) FROM placeholder_config " +
            "WHERE template_type = #{templateType}")
    int countByTemplateType(@Param("templateType") Integer templateType);
}