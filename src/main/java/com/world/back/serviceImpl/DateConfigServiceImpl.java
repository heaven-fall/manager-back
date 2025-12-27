package com.world.back.serviceImpl;

import com.world.back.entity.DateConfig;
import com.world.back.mapper.DateConfigMapper;
import com.world.back.service.DateConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DateConfigServiceImpl implements DateConfigService {

    private final DateConfigMapper dateConfigMapper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Map<String, Object>> getAllDateConfigs() {
        log.info("获取所有日期配置");
        List<DateConfig> configs = dateConfigMapper.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (DateConfig config : configs) {
            Map<String, Object> item = new HashMap<>();
            item.put("config_key", config.getConfigKey());
            item.put("config_value", config.getConfigValue() != null ?
                    dateFormat.format(config.getConfigValue()) : null);
            item.put("updated_at", config.getUpdatedAt() != null ?
                    dateTimeFormat.format(config.getUpdatedAt()) : null);
            result.add(item);
        }

        // 确保两个配置都存在
        ensureDefaultConfigs(result);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDateConfig(String key, String value) {
        log.info("保存日期配置，key: {}, value: {}", key, value);

        try {
            validateDateKey(key);
            Date date = parseDate(value);

            DateConfig dateConfig = new DateConfig();
            dateConfig.setConfigKey(key);
            dateConfig.setConfigValue(date);

            int result = dateConfigMapper.saveOrUpdate(dateConfig);

            if (result > 0) {
                log.info("日期配置保存成功，key: {}, value: {}", key, value);
                return true;
            } else {
                log.error("日期配置保存失败，key: {}, value: {}", key, value);
                return false;
            }
        } catch (Exception e) {
            log.error("保存日期配置失败", e);
            throw new RuntimeException("保存日期配置失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDateConfigs(Map<String, String> configs) {
        log.info("批量保存日期配置，configs: {}", configs);

        try {
            boolean allSuccess = true;
            List<String> errors = new ArrayList<>();

            for (Map.Entry<String, String> entry : configs.entrySet()) {
                try {
                    boolean success = saveDateConfig(entry.getKey(), entry.getValue());
                    if (!success) {
                        allSuccess = false;
                        errors.add(entry.getKey() + "=" + entry.getValue());
                    }
                } catch (Exception e) {
                    allSuccess = false;
                    errors.add(entry.getKey() + ": " + e.getMessage());
                }
            }

            if (!allSuccess && !errors.isEmpty()) {
                throw new RuntimeException("部分配置保存失败: " + String.join(", ", errors));
            }

            return allSuccess;
        } catch (Exception e) {
            log.error("批量保存日期配置失败", e);
            throw new RuntimeException("批量保存日期配置失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDefenseDate() {
        DateConfig config = dateConfigMapper.findByKey("defense_date");
        if (config != null && config.getConfigValue() != null) {
            return dateFormat.format(config.getConfigValue());
        }
        return getCurrentDate();
    }

    @Override
    public String getEvaluationDate() {
        DateConfig config = dateConfigMapper.findByKey("evaluation_date");
        if (config != null && config.getConfigValue() != null) {
            return dateFormat.format(config.getConfigValue());
        }
        return getCurrentDate();
    }

    @Override
    public Map<String, String> getDateConfigMap() {
        Map<String, String> result = new HashMap<>();
        List<DateConfig> configs = dateConfigMapper.findAll();

        for (DateConfig config : configs) {
            if (config.getConfigValue() != null) {
                result.put(config.getConfigKey(), dateFormat.format(config.getConfigValue()));
            }
        }

        // 确保两个键都存在
        if (!result.containsKey("defense_date")) {
            result.put("defense_date", getCurrentDate());
        }
        if (!result.containsKey("evaluation_date")) {
            result.put("evaluation_date", getCurrentDate());
        }

        return result;
    }

    @Override
    public void resetDatesToCurrent() {
        log.info("重置日期为当前日期");

        try {
            String currentDate = getCurrentDate();

            DateConfig defenseConfig = new DateConfig();
            defenseConfig.setConfigKey("defense_date");
            defenseConfig.setConfigValue(parseDate(currentDate));

            DateConfig evaluationConfig = new DateConfig();
            evaluationConfig.setConfigKey("evaluation_date");
            evaluationConfig.setConfigValue(parseDate(currentDate));

            dateConfigMapper.saveOrUpdate(defenseConfig);
            dateConfigMapper.saveOrUpdate(evaluationConfig);

            log.info("日期重置成功，当前日期: {}", currentDate);
        } catch (Exception e) {
            log.error("重置日期失败", e);
            throw new RuntimeException("重置日期失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsDateConfig(String key) {
        DateConfig config = dateConfigMapper.findByKey(key);
        return config != null;
    }

    private void validateDateKey(String key) {
        if (!"defense_date".equals(key) && !"evaluation_date".equals(key)) {
            throw new IllegalArgumentException("无效的配置键: " + key);
        }
    }

    private Date parseDate(String dateStr) throws ParseException {
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new ParseException("日期格式错误，应为 yyyy-MM-dd: " + dateStr, 0);
        }
    }

    private String getCurrentDate() {
        return dateFormat.format(new Date());
    }

    private void ensureDefaultConfigs(List<Map<String, Object>> configs) {
        boolean hasDefenseDate = configs.stream()
                .anyMatch(config -> "defense_date".equals(config.get("config_key")));

        boolean hasEvaluationDate = configs.stream()
                .anyMatch(config -> "evaluation_date".equals(config.get("config_key")));

        if (!hasDefenseDate) {
            Map<String, Object> defenseConfig = new HashMap<>();
            defenseConfig.put("config_key", "defense_date");
            defenseConfig.put("config_value", getCurrentDate());
            defenseConfig.put("updated_at", dateTimeFormat.format(new Date()));
            configs.add(defenseConfig);
        }

        if (!hasEvaluationDate) {
            Map<String, Object> evaluationConfig = new HashMap<>();
            evaluationConfig.put("config_key", "evaluation_date");
            evaluationConfig.put("config_value", getCurrentDate());
            evaluationConfig.put("updated_at", dateTimeFormat.format(new Date()));
            configs.add(evaluationConfig);
        }
    }
}