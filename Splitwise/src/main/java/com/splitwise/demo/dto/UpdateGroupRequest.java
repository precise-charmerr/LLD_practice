package com.splitwise.demo.dto;

public class UpdateGroupRequest {
    private String groupName;
    private Long userId;

    public UpdateGroupRequest() {}

    public UpdateGroupRequest(String groupName, Long userId) {
        this.groupName = groupName;
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
