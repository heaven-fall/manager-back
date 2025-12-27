package com.world.back.mapper;


import com.world.back.entity.Template;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TemplateMapper {

    // 获取所有模板
    @Select("SELECT * FROM template ORDER BY type")
    @Results(id = "templateMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "type"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "fileSize", column = "file_size"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "updatedBy", column = "updated_by")
    })
    List<Template> findAll();

    // 根据ID查询模板
    @Select("SELECT * FROM template WHERE id = #{id}")
    @ResultMap("templateMap")
    Template findById(@Param("id") Integer id);

    // 根据类型查询模板
    @Select("SELECT * FROM template WHERE type = #{type}")
    @ResultMap("templateMap")
    Template findByType(@Param("type") Integer type);

    // 更新模板文件信息
    @Update({
            "<script>",
            "UPDATE template ",
            "SET file_path = #{filePath}, ",
            "    file_name = #{fileName}, ",
            "    file_size = #{fileSize}, ",
            "    updated_by = #{updatedBy}, ",
            "    updated_at = NOW() ",
            "WHERE id = #{id}",
            "</script>"
    })
    int updateFileInfo(Template template);

    // 清空模板文件信息
    @Update("UPDATE template SET file_path = '', file_name = '', file_size = NULL, " +
            "updated_by = NULL, updated_at = NOW() WHERE id = #{id}")
    int clearFileInfo(@Param("id") Integer id);

    // 检查模板是否已上传
    @Select("SELECT COUNT(*) FROM template WHERE id = #{id} AND file_path IS NOT NULL AND file_path != ''")
    int checkTemplateExists(@Param("id") Integer id);
}