package com.world.back.service;

import java.util.List;
import java.util.Map;

/**
 * 日期配置服务接口
 */
public interface DateConfigService {

    /**
     * 获取所有日期配置
     * @return 日期配置列表
     */
    List<Map<String, Object>> getAllDateConfigs();

    /**
     * 保存日期配置
     * @param key 配置键
     * @param value 配置值
     * @return 是否保存成功
     */
    boolean saveDateConfig(String key, String value);

    /**
     * 批量保存日期配置
     * @param configs 配置映射
     * @return 是否保存成功
     */
    boolean saveDateConfigs(Map<String, String> configs);

    /**
     * 获取答辩日期
     * @return 答辩日期
     */
    String getDefenseDate();

    /**
     * 获取评定日期
     * @return 评定日期
     */
    String getEvaluationDate();

    /**
     * 获取日期配置映射
     * @return 配置映射
     */
    Map<String, String> getDateConfigMap();

    /**
     * 重置日期为当前日期
     */
    void resetDatesToCurrent();

    /**
     * 检查日期配置是否存在
     * @param key 配置键
     * @return 是否存在
     */
    boolean existsDateConfig(String key);
}