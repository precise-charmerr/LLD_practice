package com.splitwise.demo.repository;

import com.splitwise.demo.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByExpenseName(String expenseName);
    List<Expense> findAllByGroupId(Long id);

    @Query("SELECT DISTINCT e FROM Expense e JOIN e.expenseSplits s WHERE e.paidByUserId = :userId OR s.user.id = :userId")
    List<Expense> findAllExpensesByUser(@Param("userId") Long userId);
}
