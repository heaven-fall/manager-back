package com.world.back.serviceImpl;

import com.world.back.entity.Coefficient;
import com.world.back.entity.TeacherScore;
import com.world.back.mapper.DefenseMapper;
import com.world.back.mapper.TeacherMapper;
import com.world.back.service.DefenseService;
import com.world.back.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

import static org.apache.commons.collections4.MapUtils.getIntValue;

@Service
public class DefenseServiceImpl implements DefenseService
{
    @Autowired
    private DefenseMapper defenseMapper;
    @Autowired
    private GroupService groupService;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void yearAdd(Integer year)
    {
        defenseMapper.yearAdd(year);
    }

    @Override
    public void yearDelete(Integer year)
    {
        List<Map<String, Object>> groups = groupService.getAllGroups(year);
        for (Map<String, Object> map : groups)
        {
            groupService.deleteGroup((Integer)map.get("id"));
        }
        defenseMapper.yearDelete(year);
    }

    @Override
    public List<Map<String, Object>> yearAll()
    {
        return defenseMapper.yearAll();
    }

    @Override
    public Integer getCountByYear(Integer year)
    {
        return defenseMapper.getCountByYear(year);
    }

    @Override
    public Integer getStudentCountByYear(Integer year)
    {
        return defenseMapper.getStudentCountByYear(year);
    }

    public Boolean saveScore(Map<String, Object> map) {
        try {
            String stuId = map.get("stu_id").toString();
            String teacherId = map.get("teacher_id").toString();
            String groupId = map.get("group_id").toString();
            Integer type = Integer.parseInt(map.get("type").toString());

            String teacherName = teacherMapper.selectTeacherById(teacherId).getRealName();

            TeacherScore currentScore = buildTeacherScore(map, teacherId, teacherName, type);

            List<TeacherScore> teacherScores = getExistingTeacherScores(stuId, groupId);

            updateTeacherScoresList(teacherScores, currentScore);

            String teacherScoresJson = objectMapper.writeValueAsString(teacherScores);

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("stuId", stuId);
            updateData.put("groupId", groupId);
            updateData.put("teacherId", teacherId);
            updateData.put("teacherScoresJson", teacherScoresJson);

            int result = defenseMapper.saveScore(updateData);

            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("保存评分失败: " + e.getMessage());
        }
    }

    private void updateTeacherScoresList(List<TeacherScore> teacherScores, TeacherScore currentScore) {
        boolean found = false;
        for (int i = 0; i < teacherScores.size(); i++) {
            TeacherScore score = teacherScores.get(i);
            if (score.getTeacherId().equals(currentScore.getTeacherId())) {
                // 更新现有记录
                teacherScores.set(i, currentScore);
                found = true;
                break;
            }
        }

        // 如果没找到，添加新记录
        if (!found) {
            teacherScores.add(currentScore);
        }
    }

    private List<TeacherScore> getExistingTeacherScores(String stuId, String groupId) {
        try {
            String json = defenseMapper.selectTeacherScoresJson(stuId, groupId);

            if (json == null || json.trim().isEmpty()) {
                return new ArrayList<>();
            }

            List<TeacherScore> scores = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TeacherScore.class));

            // 过滤掉可能的 null 元素
            if (scores != null) {
                scores.removeIf(Objects::isNull);
            }

            return scores != null ? scores : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private TeacherScore buildTeacherScore(Map<String, Object> map,
                                           String teacherId,
                                           String teacherName,
                                           Integer type) {
        TeacherScore score = new TeacherScore();
        score.setTeacherId(teacherId);
        score.setTeacherRealName(teacherName);
        score.setType(type);
        score.setTotalScore(Integer.parseInt(map.get("total_score").toString()));

        if (type == 2) { // 毕业论文
            score.setPaperQuality(getIntValue(map, "paper_quality"));
            score.setPresentation(getIntValue(map, "presentation"));
            score.setQaPerformance(getIntValue(map, "qa_performance"));

            // 毕业设计字段设为null
            score.setDesignQuality1(null);
            score.setDesignQuality2(null);
            score.setDesignQuality3(null);
            score.setDesignPresentation(null);
            score.setDesignQa1(null);
            score.setDesignQa2(null);
        } else if (type == 1) { // 毕业设计
            score.setDesignQuality1(getIntValue(map, "design_quality1"));
            score.setDesignQuality2(getIntValue(map, "design_quality2"));
            score.setDesignQuality3(getIntValue(map, "design_quality3"));
            score.setDesignPresentation(getIntValue(map, "design_presentation"));
            score.setDesignQa1(getIntValue(map, "design_qa1"));
            score.setDesignQa2(getIntValue(map, "design_qa2"));

            // 毕业论文字段设为null
            score.setPaperQuality(null);
            score.setPresentation(null);
            score.setQaPerformance(null);
        }
        return score;
    }

    @Override
    public List<Map<String, Object>> getGroupFirstStudents(Integer year) {
        try {
            List<Map<String, Object>> students = defenseMapper.getGroupFirstStudents(year);

            for (Map<String, Object> student : students) {
                if (student.get("adjustmentCoefficient") != null) {
                    Double coefficient = (Double) student.get("adjustmentCoefficient");
                    student.put("adjustmentCoefficient",
                            coefficient != null ? Math.round(coefficient * 1000) / 1000.0 : null);
                }

                if (student.get("majorScore") != null) {
                    Object majorScore = student.get("majorScore");
                    if (majorScore instanceof Number) {
                        student.put("majorScore", ((Number) majorScore).intValue());
                    }
                }
                if (student.get("groupScore") != null) {
                    Object groupScore = student.get("groupScore");
                    if (groupScore instanceof Number) {
                        student.put("groupScore", ((Number) groupScore).intValue());
                    }
                }
            }
            return students;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据失败: " + e.getMessage());
        }
    }

    @Override
    public Boolean saveMajorScore(Integer groupId, String studentId, Double majorScore) {
        try {
            // 验证成绩范围
            if (majorScore < 0 || majorScore > 100) {
                throw new IllegalArgumentException("大组成绩必须在0-100之间");
            }

            // 保存到 group_defense 表
            int result = defenseMapper.saveMajorScore(groupId, studentId, majorScore);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("保存大组成绩失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> SaveCoefficients(Integer year) {
        try {
            // 获取所有小组第一名数据
            List<Map<String, Object>> firstStudents = getGroupFirstStudents(year);

            List<Coefficient> coefficientsToSave = new ArrayList<>();
            int successCount = 0;
            int skipCount = 0;

            for (Map<String, Object> student : firstStudents) {
                Integer groupId = (Integer) student.get("groupId");
                Object majorScoreObj = student.get("majorScore");
                Object groupScoreObj = student.get("groupScore");

                Double majorScore = majorScoreObj != null ? ((Number) majorScoreObj).doubleValue() : null;
                Double groupScore = groupScoreObj != null ? ((Number) groupScoreObj).doubleValue() : null;

                // 检查是否有大组成绩
                if (majorScore == null || groupScore == null || groupScore <= 0) {
                    skipCount++;
                    continue;
                }

                // 修正：使用浮点数除法
                Double coefficient = majorScore / groupScore;
                coefficient = Math.round(coefficient * 1000) / 1000.0;
                coefficient = Math.round(coefficient * 1000) / 1000.0;

                Coefficient coef= new Coefficient();
                coef.setGroupId(groupId);
                coef.setAdjustmentCoefficient(coefficient);

                coefficientsToSave.add(coef);
                successCount++;
            }

            // 批量保存调节系数
            if (!coefficientsToSave.isEmpty()) {
                defenseMapper.saveSaveCoefficients(coefficientsToSave);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", firstStudents.size());
            result.put("success", successCount);
            result.put("skipped", skipCount);
            result.put("message", String.format("已为 %d 个小组计算并保存调节系数", successCount));

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("计算调节系数失败: " + e.getMessage());
        }
    }

    @Override
    public Double getAdjustmentCoefficient(Integer groupId) {
        try {
            return defenseMapper.getAdjustmentCoefficient(groupId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取调节系数失败: " + e.getMessage());
        }
    }
}