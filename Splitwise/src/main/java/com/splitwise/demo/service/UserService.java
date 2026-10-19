package com.splitwise.demo.service;

import com.splitwise.demo.dto.*;
import com.splitwise.demo.exception.GroupAlreadyExistsException;
import com.splitwise.demo.exception.ResourceNotFoundException;
import com.splitwise.demo.exception.UserAlreadyExistsException;
import com.splitwise.demo.model.Expense;
import com.splitwise.demo.model.ExpenseSplits;
import com.splitwise.demo.model.Group;
import com.splitwise.demo.model.User;
import com.splitwise.demo.repository.ExpenseRepository;
import com.splitwise.demo.repository.GroupRepository;
import com.splitwise.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public UserService(GroupRepository groupRepository,
                       UserRepository userRepository, ExpenseRepository expenseRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public void addGroup(GroupCreateRequest groupCreateRequest) {
        Optional<Group> existingGroup = groupRepository.findByGroupName(groupCreateRequest.getGroupName());

        if (existingGroup.isPresent()) {
            throw new GroupAlreadyExistsException("A group with this name already exists!");
        }
        Set<User> users = new HashSet<>(userRepository.findAllById(groupCreateRequest.getUsers()));
        Group group = new Group(groupCreateRequest.getGroupName(), users);
        groupRepository.save(group);
    }

    @Transactional
    public void addUserToGroup(UpdateGroupRequest updateGroupRequest) {
        User user = userRepository
                .findById(updateGroupRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("This user is not present currently!"));
        Group group = groupRepository
                .findByGroupName(updateGroupRequest.getGroupName())
                .orElseThrow(() -> new ResourceNotFoundException("This group is not present currently!"));
        if(group.getUsers().contains(user)) {
            throw new UserAlreadyExistsException("User already exists in this group!");
        }
        group.getUsers().add(user);
        groupRepository.save(group);
    }

    @Transactional
    public void removeUserFromGroup(UpdateGroupRequest updateGroupRequest) {
        User user = userRepository
                .findById(updateGroupRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("This user is not present currently!"));
        Group group = groupRepository
                .findByGroupName(updateGroupRequest.getGroupName())
                .orElseThrow(() -> new ResourceNotFoundException("This group is not present currently!"));
        if(!group.getUsers().contains(user)) {
            throw new ResourceNotFoundException("User does exists in this group already!");
        }
        group.getUsers().remove(user);
        groupRepository.save(group);
    }

    @Transactional
    public void addExpenseToGroup(AddExpenseRequest addExpenseRequest) {
        List<User> users = userRepository.findAllById(addExpenseRequest.getUserIds());
        if(users.isEmpty()) {
            throw new RuntimeException("These users are not present");
        }
        Group group = groupRepository
                .findByGroupName(addExpenseRequest.getGroupName())
                .orElseThrow(() -> new RuntimeException("This group is not present!"));

        Set<User> groupUsers = group.getUsers();

        Map<Long, User> userMap = users.stream()
                .filter(groupUsers::contains)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        if (userMap.size() != users.size()) {
            throw new RuntimeException("One or more users do not belong to this group!");
        }

        Expense expense = new Expense();
        expense.setGroupId(group.getId());
        expense.setAmount(addExpenseRequest.getAmount());
        expense.setExpenseName(addExpenseRequest.getExpenseName());
        expense.setPaidByUserId(addExpenseRequest.getPaidByUserId());

        Set<ExpenseSplits> expenseSplits = new HashSet<>();
        boolean percentageGiven = addExpenseRequest.getPercent() != null && !addExpenseRequest.getPercent().isEmpty();
        if(percentageGiven && addExpenseRequest.getPercent().size() != addExpenseRequest.getUserIds().size()) {
            throw new RuntimeException("Percentage and Users list should be of same size");
        }
        for(int i = 0; i< addExpenseRequest.getUserIds().size(); i++) {
            Long userId = addExpenseRequest.getUserIds().get(i);
            User currentUser = userMap.get(userId);

            if (currentUser == null) {
                throw new RuntimeException("User with ID " + userId + " not found!");
            }

            ExpenseSplits expenseSplit = new ExpenseSplits(
                    expense,
                     currentUser,
                    percentageGiven ? addExpenseRequest.getPercent().get(i) : (100.0 / addExpenseRequest.getUserIds().size())
            );
            expenseSplits.add(expenseSplit);
        }
        expense.setExpenseSplits(expenseSplits);
        expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(DeleteExpenseRequest deleteExpenseRequest) {
        Expense expense = expenseRepository
                .findByExpenseName(deleteExpenseRequest.getExpenseName())
                .orElseThrow(() -> new ResourceNotFoundException("This expense does not exists already"));
        Group group = groupRepository
                .findByGroupName(deleteExpenseRequest.getGroupName())
                .orElseThrow(() -> new ResourceNotFoundException("This group does not exist!"));
        if(!expense.getGroupId().equals(group.getId())) {
            throw new RuntimeException("This expense does not belong to this group!!");
        }
        expenseRepository.delete(expense);
    }

    @Transactional
    public Map<Long, Double> settleTransaction(TransactionSettlementRequest transactionSettlementRequest) {
        Group group = groupRepository
                .findByGroupName(transactionSettlementRequest.getGroupName())
                .orElseThrow(() -> new ResourceNotFoundException("This group does not exist in system yet!"));
        List<Expense> expenseInGroup = expenseRepository.findAllByGroupId(group.getId());
        if(expenseInGroup.isEmpty()) {
            throw new RuntimeException("No expenses have been added in this group yet!!");
        }
        Map<DebtRecord, Double> debtRecordTrack = new HashMap<>();
        for (Expense expense : expenseInGroup) {
            Set<ExpenseSplits> expenseSplits = expense.getExpenseSplits();
            for (ExpenseSplits expenseSplits1 : expenseSplits) {
                DebtRecord debtRecord = new DebtRecord(expenseSplits1.getUser().getId(), expense.getPaidByUserId());
                if(expenseSplits1.getUser().getId().equals(expense.getPaidByUserId())) {
                    continue;
                }
                debtRecordTrack.put(debtRecord, debtRecordTrack.getOrDefault(debtRecord, 0.0) + expense.getAmount() * (expenseSplits1.getPercentage() / 100));
            }
        }
        Map<Long, Double> settlement = new HashMap<>();
        debtRecordTrack.forEach((record, amount) -> {
            if(Objects.equals(record.fromUserId(), transactionSettlementRequest.getUserId())) {
                settlement.put(record.toUserId(), settlement.getOrDefault(record.toUserId(), 0.0) - amount);
            } else if(Objects.equals(record.toUserId(), transactionSettlementRequest.getUserId())) {
                settlement.put(record.fromUserId(), settlement.getOrDefault(record.fromUserId(), 0.0) + amount);
            }
        });
        return settlement;
    }
}
