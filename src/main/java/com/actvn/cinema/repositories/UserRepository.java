package com.actvn.cinema.repositories;

import com.actvn.cinema.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(final String username);

    User findByEmail(final String email);

    List<User> findUserByRole(String role);

    List<User> findByUsernameContainingOrEmailContainingAndRole(String username, String email, String role);

}