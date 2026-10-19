package com.splitwise.demo.dto;

public class DeleteExpenseRequest {
    private String groupName;
    private String expenseName;

    public DeleteExpenseRequest() {}

    public DeleteExpenseRequest(String groupName, String expenseName) {
        this.groupName = groupName;
        this.expenseName = expenseName;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
