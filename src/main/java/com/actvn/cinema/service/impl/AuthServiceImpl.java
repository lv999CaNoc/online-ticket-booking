package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Token;
import com.actvn.cinema.model.User;
import com.actvn.cinema.repositories.TokenRepository;
import com.actvn.cinema.repositories.UserRepository;
import com.actvn.cinema.service.AuthService;
import com.actvn.cinema.service.MailService;
import com.actvn.cinema.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Value("${site.base.url}")
    private String BASE_URL;

    @Override
    public boolean login(final Principal principal) {
        return  (principal != null && ((Authentication) principal).isAuthenticated());
    }

    @Override
    public User register(@Valid User user) throws IllegalArgumentException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        log.info("[LOG] Saving user: {}", user);
        sendToken(user);
        return user;
    }

    @Override
    public void sendToken(final User user) {
        final String tokenValue = UUID.randomUUID().toString();
        final Token token = new Token();
        token.setValue(tokenValue);
        token.setUser(user);
        tokenRepository.save(token);

        String subject = "[Cinema] Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Actvn Cinema App.";

        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = BASE_URL + "/verify?token=" + token.getValue();

        content = content.replace("[[URL]]", verifyURL);

        try {
            mailService.sendMail(user.getEmail(), subject, content, true);
            log.info("[LOG] Send token to email: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("[LOG] Failed to send token to email {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Override
    public void verifyEmail(String tokenValue) throws NotFoundException {
        Token token = tokenRepository.findByValue(tokenValue);
        if (Objects.isNull(token)) {
            log.error("[LOG] Token not found: {}", tokenValue);
            throw new NotFoundException("Token not found: " + tokenValue);
        }
        Optional<User> user = userRepository.findById(token.getUser().getId());
        if (Objects.isNull(user)) {
            log.error("[LOG] User not found for token: {}", token);
            throw new NotFoundException("User not found for token: " + token);
        }

        log.info("[LOG] Verifying email successfully: {}", user.get().getEmail());
        User verifyUser = user.get();
        verifyUser.setEnabled(true);
        userRepository.save(verifyUser);
        tokenRepository.delete(token);
    }

}
