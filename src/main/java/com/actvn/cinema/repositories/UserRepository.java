package com.actvn.cinema.repositories;

import com.actvn.cinema.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(final String username);

    User findByEmail(final String email);

    List<User> findUserByRole(String role);

    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email LIKE %:email% AND u.role = :role")
    List<User> findByUsernameOrEmailContainingAndRole(@Param("username") String username,
                                                      @Param("email") String email,
                                                      @Param("role") String role);

}