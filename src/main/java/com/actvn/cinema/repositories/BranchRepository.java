package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    @Query("SELECT b FROM Branch b WHERE b.id IN\n" +
            "(SELECT r.branch.id FROM Room r WHERE r.id IN\n" +
            "(SELECT s.room.id FROM Schedule s WHERE s.movie.id=:movieId))")
    List<Branch> getBranchThatShowTheMovie(@Param("movieId") Integer movieId);


    List<Branch> findBranchByNameContainingIgnoreCase(String name);

    List<Branch> findBranchByThanhPho(String city);

    @Query("SELECT DISTINCT b.thanhPho FROM Branch b")
    List<String> getAllThanhPho();
}
