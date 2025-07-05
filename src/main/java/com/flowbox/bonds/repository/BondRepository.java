package com.flowbox.bonds.repository;

import com.flowbox.bonds.model.Bond;
import com.flowbox.bonds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BondRepository extends JpaRepository<Bond, Long> {
    List<Bond> findByUser(User user);
    Optional<Bond> findByIdAndUser(Long id, User user);
}
