package com.flowbox.bonds.repository;

import com.flowbox.bonds.model.Bond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BondRepository extends JpaRepository<Bond, Long> {
}
