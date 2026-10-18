package com.splitwise.demo.service;

import com.splitwise.demo.dto.UpdateGroupRequest;
import com.splitwise.demo.dto.GroupCreateRequest;
import com.splitwise.demo.exception.GroupAlreadyExistsException;
import com.splitwise.demo.exception.ResourceNotFoundException;
import com.splitwise.demo.exception.UserAlreadyExistsException;
import com.splitwise.demo.model.Group;
import com.splitwise.demo.model.User;
import com.splitwise.demo.repository.GroupRepository;
import com.splitwise.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class UserService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public UserService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
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
                .findByGroupName(updateGroupRequest.getGroup())
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
                .findByGroupName(updateGroupRequest.getGroup())
                .orElseThrow(() -> new ResourceNotFoundException("This group is not present currently!"));
        if(!group.getUsers().contains(user)) {
            throw new ResourceNotFoundException("User does exists in this group already!");
        }
        group.getUsers().remove(user);
        groupRepository.save(group);
    }
}
