package com.splitwise.demo.model;

import com.splitwise.demo.dto.ExpenseRequest;
import jakarta.persistence.*;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double percentage;

    // constructors
    public ExpenseSplits() {}

    public ExpenseSplits(Expense expense, User user, Double percentage) {
        this.expense = expense;
        this.user = user;
        this.percentage = percentage;
    }


    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
