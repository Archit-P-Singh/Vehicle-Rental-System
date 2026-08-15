package com.fleetflow.auth.repository;

import com.fleetflow.auth.domain.entity.DrivingLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrivingLicenseRepository extends JpaRepository<DrivingLicense, String> {
    Optional<DrivingLicense> findByUserId(String userId);
}
