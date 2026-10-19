package com.splitwise.demo.dto;

public class TransactionSettlementRequest {
    private Long userId;
    private String groupName;

    public TransactionSettlementRequest() {}

    public TransactionSettlementRequest(Long userId, String groupName) {
        this.userId = userId;
        this.groupName = groupName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
