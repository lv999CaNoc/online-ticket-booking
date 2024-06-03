package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT DISTINCT c.id FROM Movie m JOIN m.categories c WHERE m.id IN :movies")
    List<Integer> getCategoryIdByMovieIdIn(@Param("movies") List<Integer> movieIds);

}
