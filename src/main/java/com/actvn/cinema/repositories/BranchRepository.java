package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    List<Branch> findBranchByNameContainingIgnoreCase(String name);

    List<Branch> findBranchByThanhPho(String city);

}
