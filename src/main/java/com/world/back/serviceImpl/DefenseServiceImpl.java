package com.world.back.serviceImpl;

import com.world.back.entity.info.AdvisorInfo;
import com.world.back.entity.info.DefenseGroupInfo;
import com.world.back.entity.info.DefenseInfo;
import com.world.back.entity.info.StudentScores;
import com.world.back.entity.user.Teacher;
import com.world.back.entity.TeacherScore;
import com.world.back.mapper.DefenseMapper;
import com.world.back.mapper.TeacherMapper;
import com.world.back.service.DefenseService;
import com.world.back.service.GroupService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DefenseServiceImpl implements DefenseService
{
    @Autowired
    private DefenseMapper defenseMapper;
    @Autowired
    private GroupService groupService;
    @Autowired
    private TeacherMapper teacherMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Override
    public DefenseGroupInfo getCurrentGroup(String teacherId) {
        try {
            // 查询教师当前所在的小组
            DefenseGroupInfo groupInfo = defenseMapper.selectCurrentGroup(teacherId);

            if (groupInfo == null) {
                return null;
            }

            // 设置教师ID
            groupInfo.setTeacherId(teacherId);

            // 查询统计信息
            Integer groupId = groupInfo.getGroupId();
            groupInfo.setTotalStudentCount(defenseMapper.countStudentsByGroupId(groupId));
            groupInfo.setScoredStudentCount(defenseMapper.countScoredStudentsByGroupId(groupId));

            if (groupInfo.getAdminName() != null) {
                String adminName = defenseMapper.selectAdminNameById(groupInfo.getAdminName());
                groupInfo.setAdminName(adminName);
            }

            return groupInfo;
        } catch (Exception e) {
            throw new RuntimeException("获取当前答辩小组信息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DefenseInfo> getGroupStudents(Integer groupId, String teacherId) {
        try {
            // 1. 获取教师是否为答辩组长
            List<Teacher.GroupInfo> teacherGroups = teacherMapper.selectTeacherGroups(teacherId);
            boolean isDefenseLeader = teacherGroups.stream()
                    .anyMatch(group -> group.getGroupId() != null &&
                            group.getGroupId().equals(groupId) &&
                            Boolean.TRUE.equals(group.getIsDefenseLeader()));

            // 2. 获取学生答辩信息
            List<DefenseInfo> students = defenseMapper.selectStudentsByGroupId(groupId);

            // 3. 获取指导教师信息
            List<AdvisorInfo> advisors = defenseMapper.selectAdvisorsByGroupId(groupId);
            Map<String, AdvisorInfo> advisorMap = advisors.stream()
                    .collect(Collectors.toMap(AdvisorInfo::getStudentId, advisor -> advisor));

            // 4. 获取成绩信息
            List<StudentScores> scoresList = defenseMapper.selectScoresByGroupId(groupId);
            Map<String, StudentScores> scoresMap = scoresList.stream()
                    .collect(Collectors.toMap(StudentScores::getStudentId, scores -> scores));

            // 5. 组装数据
            for (DefenseInfo student : students) {
                String studentId = student.getStudentId();

                // 设置指导教师信息
                AdvisorInfo advisor = advisorMap.get(studentId);
                if (advisor != null) {
                    student.setAdvisorId(advisor.getAdvisorId());
                    student.setAdvisorName(advisor.getAdvisorName());
                    student.setGuidanceYear(advisor.getGuidanceYear());
                }

                // 设置成绩信息
                StudentScores scores = scoresMap.get(studentId);
                if (scores != null) {
                    student.setTotalScore(scores.getTotalScore());
                    student.setComment(scores.getComment());
                    student.setGradedBy(scores.getGradedBy());

                    // 设置具体成绩字段
                    if (student.getDefenseType() == 0) { // 毕业论文
                        student.setPaperQuality(scores.getPaperQuality());
                        student.setPresentation(scores.getPresentation());
                        student.setQaPerformance(scores.getQaPerformance());
                    } else { // 毕业设计
                        student.setDesignQuality1(scores.getDesignQuality1());
                        student.setDesignQuality2(scores.getDesignQuality2());
                        student.setDesignQuality3(scores.getDesignQuality3());
                        student.setDesignPresentation(scores.getDesignPresentation());
                        student.setDesignQa1(scores.getDesignQa1());
                        student.setDesignQa2(scores.getDesignQa2());
                    }

                    // 解析teacher_scores字段
                    if (scores.getTeacherScoresJson() != null && !scores.getTeacherScoresJson().trim().isEmpty()) {
                        try {
                            // 使用正确的TypeReference导入
                            List<TeacherScore> teacherScores = objectMapper.readValue(
                                    scores.getTeacherScoresJson(),
                                    new TypeReference<>() {
                                    }
                            );
                            student.setTeacherScores(teacherScores);
                        } catch (Exception e) {
                            student.setTeacherScores(new ArrayList<>());
                        }
                    } else {
                        student.setTeacherScores(new ArrayList<>());
                    }
                } else {
                    // 如果没有成绩记录，初始化空的teacherScores
                    student.setTeacherScores(new ArrayList<>());
                }

                // 设置状态标志
                student.setIsScored(student.getTotalScore() != null && student.getTotalScore() > 0);
                student.setScoreStatus(getScoreStatus(student.getTotalScore()));

                // 如果是答辩组长，需要加载完整的其他教师评分
                if (isDefenseLeader && student.getTeacherScores().isEmpty()) {
                    // 加载完整的教师评分信息
                    loadCompleteTeacherScoresForLeader(student, groupId);
                }
            }

            return students;
        } catch (Exception e) {
            throw new RuntimeException("获取答辩小组学生列表失败: " + e.getMessage());
        }
    }

    private void loadCompleteTeacherScoresForLeader(DefenseInfo student, Integer groupId) {
        try {
            // 1. 获取该学生的所有评分记录（包括所有教师的评分）
            List<Map<String, Object>> allScores = defenseMapper.selectAllTeacherScoresForStudent(
                    student.getStudentId(), groupId);

            if (allScores == null || allScores.isEmpty()) {
                student.setTeacherScores(new ArrayList<>());
                return;
            }

            // 2. 转换为TeacherScore列表
            List<TeacherScore> teacherScores = new ArrayList<>();
            for (Map<String, Object> scoreRecord : allScores) {
                TeacherScore teacherScore = new TeacherScore();
                teacherScore.setTeacherId((String) scoreRecord.get("teacher_id"));
                teacherScore.setTeacherName((String) scoreRecord.get("teacher_name"));

                // 设置总分
                Integer totalScore = (Integer) scoreRecord.get("total_score");
                teacherScore.setTotalScore(totalScore != null ? totalScore : 0);

                // 设置具体成绩字段
                teacherScore.setPaperQuality((Integer) scoreRecord.get("paper_quality"));
                teacherScore.setPresentation((Integer) scoreRecord.get("presentation"));
                teacherScore.setQaPerformance((Integer) scoreRecord.get("qa_performance"));
                teacherScore.setDesignQuality1((Integer) scoreRecord.get("design_quality1"));
                teacherScore.setDesignQuality2((Integer) scoreRecord.get("design_quality2"));
                teacherScore.setDesignQuality3((Integer) scoreRecord.get("design_quality3"));
                teacherScore.setDesignPresentation((Integer) scoreRecord.get("design_presentation"));
                teacherScore.setDesignQa1((Integer) scoreRecord.get("design_qa1"));
                teacherScore.setDesignQa2((Integer) scoreRecord.get("design_qa2"));

                teacherScores.add(teacherScore);
            }

            student.setTeacherScores(teacherScores);

        } catch (Exception e) {
            // 如果发生异常，设置空列表
            student.setTeacherScores(new ArrayList<>());
        }
    }

    @Override
    public List<DefenseInfo> getGroupStudentsForLeader(Integer groupId) {
        // 为答辩组长获取更详细的信息
        List<DefenseInfo> students = defenseMapper.selectStudentsByGroupId(groupId);

        // 为每个学生加载完整的教师评分信息
        for (DefenseInfo student : students) {
            loadCompleteTeacherScoresForLeader(student, groupId);
        }

        return students;
    }

    // 根据总分获取状态文本
    private String getScoreStatus(Integer totalScore) {
        if (totalScore == null || totalScore == 0) {
            return "未评分";
        } else if (totalScore >= 90) {
            return "优秀";
        } else if (totalScore >= 80) {
            return "良好";
        } else if (totalScore >= 70) {
            return "中等";
        } else if (totalScore >= 60) {
            return "及格";
        } else {
            return "不及格";
        }
    }
}