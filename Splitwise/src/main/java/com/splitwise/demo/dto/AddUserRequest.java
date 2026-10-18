package com.splitwise.demo.dto;

import com.splitwise.demo.model.User;

public class AddUserRequest {
    private String group;
    private Long userId;

    public AddUserRequest() {}

    public AddUserRequest(String group, Long userId) {
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
