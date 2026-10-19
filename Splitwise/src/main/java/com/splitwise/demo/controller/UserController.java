package com.splitwise.demo.controller;

import com.splitwise.demo.dto.*;
import com.splitwise.demo.service.AuthService;
import com.splitwise.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<String> signupUser(@RequestBody SignupRequest signupRequest) throws Exception {
        authService.registerUser(signupRequest);
        return ResponseEntity.ok("User signed up successfully");
    }

    @PostMapping(path = "/auth/signin")
    public ResponseEntity<String> signinUser(@RequestBody SigninRequest signinRequest) {
        authService.authenticateUser(signinRequest);
        return ResponseEntity.ok("User signed in successfully");
    }

    @PostMapping(path = "/groups")
    public ResponseEntity<String> createGroup(@RequestBody GroupCreateRequest groupCreateRequest) {
        userService.addGroup(groupCreateRequest);
        return ResponseEntity.ok("group created successfully");
    }

    @PostMapping(path = "/groups/addUser")
    public ResponseEntity<String> addUserToGroup(@RequestBody UpdateGroupRequest updateGroupRequest) {
        userService.addUserToGroup(updateGroupRequest);
        return ResponseEntity.ok("User added in group successfully");
    }

    @PostMapping(path = "/groups/removeUser")
    public ResponseEntity<String> removeUserFromGroup(@RequestBody UpdateGroupRequest updateGroupRequest) {
        userService.removeUserFromGroup(updateGroupRequest);
        return ResponseEntity.ok("User removed from group successfully");
    }

    @PostMapping(path = "/expense/addExpense")
    public ResponseEntity<String> addExpenseToGroup(@RequestBody AddExpenseRequest addExpenseRequest) {
        userService.addExpenseToGroup(addExpenseRequest);
        return ResponseEntity.ok("Expense added to group successfully");
    }

    @PostMapping(path = "/expense/deleteExpense")
    public ResponseEntity<String> deleteExpense(@RequestBody DeleteExpenseRequest deleteExpenseRequest) {
        userService.deleteExpense(deleteExpenseRequest);
        return ResponseEntity.ok("Expense deleted from group successfully");
    }

    @PostMapping(path = "/expense/settleTransaction")
    public ResponseEntity<Map<Long, Double>> settleTransaction(@RequestBody TransactionSettlementRequest transactionSettlementRequest) {
        Map<Long, Double> settleRecord = userService.settleTransaction(transactionSettlementRequest);
        return ResponseEntity.ok(settleRecord);
    }
}
