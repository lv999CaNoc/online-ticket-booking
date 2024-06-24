package com.actvn.cinema.service;

import com.actvn.cinema.exception.UserNotFoundException;
import com.actvn.cinema.model.User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;

public interface UserService {

    String register(final User user, final BindingResult bindingResult);

    String verifyEmail(String value);

    List<User> findUserByRole(String role);

    List<User> findUserByUsernameOrEmail(String search, String role);

    User get(Long id) throws UserNotFoundException;

    void delete(Long id) throws UserNotFoundException;

    void lock(Long id) throws UserNotFoundException;

    void unlock(Long id) throws UserNotFoundException;

    void save(User user);
}
