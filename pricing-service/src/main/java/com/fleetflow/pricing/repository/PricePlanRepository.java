package com.fleetflow.pricing.repository;

import com.fleetflow.pricing.domain.entity.PricePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricePlanRepository extends JpaRepository<PricePlan, String> {
    Optional<PricePlan> findByName(String name);
}
