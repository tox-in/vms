package com.vms.repository;

import com.vms.model.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByEmail(String email);
    Optional<Owner> findByNationalId(String nationalId);
    Page<Owner> findAll(Pageable pageable);
    Page<Owner> findByNationalIdContainingOrEmailContaining(String nationalId, String email, Pageable pageable);
    Page<Owner> findByNationalIdContainingOrPhoneContaining(String nationalId, String phone, Pageable pageable);
}
