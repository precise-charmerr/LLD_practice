package com.splitwise.demo.repository;

import com.splitwise.demo.model.SettlementTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<SettlementTransaction, Long> {
    Optional<SettlementTransaction> findByGroupIdAndFromUserIdAndToUserIdAndStatus(Long id, Long aLong, Long userId, SettlementTransaction.Status status);
    List<SettlementTransaction> findByGroupIdAndStatus(Long id, SettlementTransaction.Status status);
}
