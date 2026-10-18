package com.splitwise.demo.dto;

public class UpdateGroupRequest {
    private String group;
    private Long userId;

    public UpdateGroupRequest() {}

    public UpdateGroupRequest(String group, Long userId) {
        this.group = group;
        this.userId = userId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
