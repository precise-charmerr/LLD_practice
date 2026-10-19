package com.splitwise.demo.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "expense")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String expenseName;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Long groupId;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExpenseSplits> expenseSplits;

    // constructors
    public Expense() {}

    public Expense(String expenseName, Long groupId, Set<ExpenseSplits> expenseSplits, Double amount) {
        this.expenseName = expenseName;
        this.groupId = groupId;
        this.expenseSplits = expenseSplits;
        this.amount = amount;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Set<ExpenseSplits> getExpenseSplits() {
        return expenseSplits;
    }

    public void setExpenseSplits(Set<ExpenseSplits> expenseSplits) {
        this.expenseSplits = expenseSplits;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}