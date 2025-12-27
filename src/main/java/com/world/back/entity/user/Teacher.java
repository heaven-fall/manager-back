package com.world.back.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends BaseUser {

    private String instituteName;

    private Integer groupId;
    private Integer groupYear;
    private Boolean isDefenseLeader = false;

    private Boolean isAdmin = false;

    private Integer guidedStudentsCount;

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
