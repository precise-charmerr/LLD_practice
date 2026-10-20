package com.splitwise.demo.dto;

import java.time.LocalDateTime;

public record ExpenseHistoryDto(
        Long expenseId,
        Double amount,
        Long paidByUserId
) {}