package com.world.back.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeacherScore {
    @JsonProperty("teacher_id")
    private String teacherId;
    @JsonProperty("teacher_name")
    private String teacherRealName;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("total_score")
    private Integer totalScore;

    // 毕业论文评分项
    @JsonProperty("paper_quality")
    private Integer paperQuality;
    @JsonProperty("presentation")
    private Integer presentation;
    @JsonProperty("qa_performance")
    private Integer qaPerformance;

    // 毕业设计评分项
    @JsonProperty("design_quality1")
    private Integer designQuality1;
    @JsonProperty("design_quality2")
    private Integer designQuality2;
    @JsonProperty("design_quality3")
    private Integer designQuality3;
    @JsonProperty("design_presentation")
    private Integer designPresentation;
    @JsonProperty("design_qa1")
    private Integer designQa1;
    @JsonProperty("design_qa2")
    private Integer designQa2;    // 回答问题分项2
}