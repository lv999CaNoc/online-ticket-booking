package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.Rated;
import com.actvn.cinema.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> findByReleaseDateBeforeAndEndDateAfter(Date releaseDate, Date endDate);

    List<Movie> findByReleaseDateAfter(Date localDate);

    List<Movie> findTop3ByOrderByLikePercentageDesc();

    @Query("SELECT m FROM Movie m WHERE LOWER(CONCAT(m.title,' ',m.actors,' ',m.director)) LIKE CONCAT('%', LOWER(?1), '%')")
    List<Movie> searchAdvantage(String keyword);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categories c WHERE c.id IN :categories " +
            "AND m.rated IN (:rateOptions) " +
            "AND m.releaseDate <= :now AND m.endDate >= :now")
    List<Movie> filterMovieNowShowing(@Param("rateOptions") List<Rated> rateOptions,
                            @Param("categories") List<Integer> categories,
                            @Param("now") Date now);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categories c WHERE c.id IN :categories " +
            "AND m.rated IN (:rateOptions) " +
            "AND m.releaseDate >= :now")
    List<Movie> filterMovieComingSoon(@Param("rateOptions") List<Rated> rateOptions,
                                      @Param("categories") List<Integer> categories,
                                      @Param("now") Date now);

    @Query("SELECT m FROM Movie m WHERE m.id IN (SELECT t.schedule.movie.id FROM Ticket t WHERE t IN (:listTicket))")
    List<Movie> findDistinctByTickets(@Param("listTicket") List<Ticket> ticketsByUserId);
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categories c WHERE c.id IN :categoriesId ")
    List<Movie> findByCategoryIdIn(@Param("categoriesId") List<Integer> categories);
}
