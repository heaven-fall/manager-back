package com.world.back.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends BaseUser {

    private String realName;

    @JsonProperty("name")
    public String getName() {
        return this.realName;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.realName = name;
    }

    private Integer instituteId;
    private String instituteName;

    private Integer groupId;
    private Integer groupYear;
    private Boolean isDefenseLeader = false;

    private Boolean isAdmin = false;
    private Integer role;

    private String title;
    private String department;
    private Integer guidedStudentsCount;

    private List<GroupInfo> groups;

    @Data
    public static class GroupInfo {
        private Integer groupId;
        private Integer groupYear;
        private Boolean isDefenseLeader;

        public GroupInfo() {}

        public GroupInfo(Integer groupId, Integer groupYear, Boolean isDefenseLeader) {
            this.groupId = groupId;
            this.groupYear = groupYear;
            this.isDefenseLeader = isDefenseLeader != null ? isDefenseLeader : false;
        }

        @JsonProperty("groupId")
        public Integer getGroupId() {
            return groupId;
        }

        @JsonProperty("groupYear")
        public Integer getGroupYear() {
            return groupYear;
        }

        @JsonProperty("isDefenseLeader")
        public Boolean getIsDefenseLeader() {
            return isDefenseLeader != null ? isDefenseLeader : false;
        }
    }
}