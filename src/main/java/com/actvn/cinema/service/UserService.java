package com.actvn.cinema.service;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserService {

    User getUserByEmail(String email) throws NotFoundException;

    User getUserByUsername(String username) throws NotFoundException;

    boolean userEmailExists(String email);

    boolean userUsernameExists(String username);

    List<User> findUserByRole(String role) throws NotFoundException;

    List<User> findUserByUsernameOrEmail(String search, String role);

    User get(Long id) throws NotFoundException;

    void delete(Long id) throws NotFoundException;

    void lock(Long id) throws NotFoundException;

    void unlock(Long id) throws NotFoundException;

    void save(User user) throws IllegalArgumentException;

    void update(@Valid User user) throws IllegalArgumentException, NotFoundException;
}
