// TemplateMapper.java - 修正 getDateConfig 方法
package com.world.back.mapper;

import com.world.back.entity.Template;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface TemplateMapper {

    @Select("SELECT * FROM template WHERE id = #{id}")
    Template selectById(Integer id);

    @Update("UPDATE template SET file_path = #{filePath}, file_name = #{fileName}, " +
            "file_size = #{fileSize}, updated_by = #{updatedBy}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateById(Template template);

    @Select("SELECT t.*, " +
            "GROUP_CONCAT(pc.placeholder_key ORDER BY pc.placeholder_key) as placeholder_keys " +
            "FROM template t " +
            "LEFT JOIN placeholder_config pc ON t.type = pc.template_type " +
            "GROUP BY t.id " +
            "ORDER BY t.type")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "hasTemplate", column = "file_path",
                    javaType = Boolean.class,
                    one = @One(select = "com.world.back.mapper.TemplateMapper.hasTemplate"))
    })
    List<Template> selectAllWithPlaceholders();

    @Select("SELECT CASE WHEN #{filePath} IS NOT NULL AND #{filePath} != '' THEN true ELSE false END")
    Boolean hasTemplate(String filePath);

    @Select("SELECT placeholder_key FROM placeholder_config WHERE template_type = #{type} AND is_required = true")
    List<String> getRequiredPlaceholdersByType(Integer type);

    @Insert("INSERT INTO date_config (config_key, config_value) " +
            "VALUES (#{key}, #{value}) " +
            "ON DUPLICATE KEY UPDATE config_value = #{value}, updated_at = CURRENT_TIMESTAMP")
    int saveDateConfig(@Param("key") String key, @Param("value") String value);

    // 修正：使用 Object 类型接收数据库返回值
    @Select("SELECT config_key, config_value FROM date_config")
    List<Map<String, Object>> getDateConfig();
}