package com.splitwise.demo.dto;

import java.util.ArrayList;

public class GroupCreateRequest {
    private String groupName;
    private ArrayList<Long> users;

    // constructor
    public GroupCreateRequest(String groupName, ArrayList<Long> users) {
        this.groupName = groupName;
        this.users = users;
    }

    // getters and setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<Long> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Long> users) {
        this.users = users;
    }
}
