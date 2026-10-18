package com.splitwise.demo.repository;

import com.splitwise.demo.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
