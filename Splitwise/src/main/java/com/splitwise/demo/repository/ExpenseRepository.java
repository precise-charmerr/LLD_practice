package com.splitwise.demo.repository;

import com.splitwise.demo.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByExpenseName(String expenseName);
    List<Expense> findAllByGroupId(Long id);
}
