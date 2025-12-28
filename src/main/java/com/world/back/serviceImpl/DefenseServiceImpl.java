package com.world.back.serviceImpl;

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
}