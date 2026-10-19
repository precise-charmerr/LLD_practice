package com.splitwise.demo.service;

import com.splitwise.demo.dto.ExpenseRequest;
import com.splitwise.demo.dto.UpdateGroupRequest;
import com.splitwise.demo.dto.GroupCreateRequest;
import com.splitwise.demo.exception.ExpenseAlreayExistsException;
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
    public void addExpenseToGroup(ExpenseRequest expenseRequest) {
        List<User> users = userRepository.findAllById(expenseRequest.getUserIds());
        if(users.isEmpty()) {
            throw new RuntimeException("These users are not present");
        }
        Group group = groupRepository
                .findByGroupName(expenseRequest.getGroupName())
                .orElseThrow(() -> new RuntimeException("This group is not present!"));

        Map<Long, User> userMap = users
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Expense expense = new Expense();
        expense.setGroupId(group.getId());
        expense.setAmount(expenseRequest.getAmount());
        expense.setExpenseName(expenseRequest.getExpenseName());

        Set<ExpenseSplits> expenseSplits = new HashSet<>();
        boolean percentageGiven = expenseRequest.getPercent() != null && !expenseRequest.getPercent().isEmpty();
        if(percentageGiven && expenseRequest.getPercent().size() != expenseRequest.getUserIds().size()) {
            throw new RuntimeException("Percentage and Users list should be of same size");
        }
        for(int i = 0;i<expenseRequest.getUserIds().size();i++) {
            Long userId = expenseRequest.getUserIds().get(i);
            User currentUser = userMap.get(userId);

            if (currentUser == null) {
                throw new RuntimeException("User with ID " + userId + " not found!");
            }

            ExpenseSplits expenseSplit = new ExpenseSplits(
                    expense,
                     currentUser,
                    percentageGiven ? expenseRequest.getPercent().get(i) : (100.0 / expenseRequest.getUserIds().size())
            );
            expenseSplits.add(expenseSplit);
        }
        expense.setExpenseSplits(expenseSplits);
        expenseRepository.save(expense);
    }
}
