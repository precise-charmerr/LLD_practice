package com.splitwise.demo.service;

import com.splitwise.demo.dto.GroupCreateRequest;
import com.splitwise.demo.model.Group;
import com.splitwise.demo.model.User;
import com.splitwise.demo.repository.GroupRepository;
import com.splitwise.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public UserService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public void addGroup(GroupCreateRequest groupCreateRequest) {
        List<User> users = userRepository.findAllById(groupCreateRequest.getUsers());
        Group group = new Group(groupCreateRequest.getGroupName(), users);
        groupRepository.save(group);
    }
}
