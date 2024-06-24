package com.actvn.cinema.service;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.User;

import javax.validation.Valid;
import java.security.Principal;

public interface AuthService {

    boolean login(final Principal principal);

    User register(@Valid User user) throws IllegalArgumentException;

    void sendToken(User user);

    void verifyEmail(String tokenValue) throws NotFoundException;
}
