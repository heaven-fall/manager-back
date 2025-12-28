package com.world.back.entity.info;

import lombok.Data;

@Data
public class StudentScores {
    private String studentId;
    private Integer type;
    private Integer totalScore;
    private String comment;
    private Integer paperQuality;
    private Integer presentation;
    private Integer qaPerformance;
    private Integer designQuality1;
    private Integer designQuality2;
    private Integer designQuality3;
    private Integer designPresentation;
    private Integer designQa1;
    private Integer designQa2;
    private String teacherScoresJson;
    private String gradedBy;
}