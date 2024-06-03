package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    List<Manager> findByBranchNameContainingIgnoreCase(String keyword);
}
