package com.github.zachsand.hs.deck.generator.data.jpa.repository;

import com.github.zachsand.hs.deck.generator.data.jpa.entity.DataPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link DataPolicyRepository}.
 */
public interface DataPolicyRepository extends JpaRepository<DataPolicyEntity, Integer> {
}
