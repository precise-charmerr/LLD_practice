package com.splitwise.demo.dto;

public record PaymentSettlementRequest (
        String groupName,
        Long fromUserId,
        Long toUserId
) {}
