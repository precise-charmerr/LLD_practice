package com.splitwise.demo.dto;

import java.util.List;

public class ExpenseRequest {
    private String groupName;
    private String expenseName;
    private List<Long> userIds;
    private Double amount;
    private List<Double> percent;

    public ExpenseRequest() {}

    public ExpenseRequest(String groupName, List<Long> userIds, Double amount, List<Double> percent, String expenseName) {
        this.groupName = groupName;
        this.userIds = userIds;
        this.amount = amount;
        this.percent = percent;
        this.expenseName = expenseName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<Double> getPercent() {
        return percent;
    }

    public void setPercent(List<Double> percent) {
        this.percent = percent;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }
}
