package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.UserNotFoundException;
import com.actvn.cinema.model.Token;
import com.actvn.cinema.model.User;
import com.actvn.cinema.repositories.TokenRepository;
import com.actvn.cinema.repositories.UserRepository;
import com.actvn.cinema.service.MailService;
import com.actvn.cinema.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${site.base.url}")
    private String BASE_URL;

    @Override
    public String login(final Principal principal) {
        if (principal != null && ((Authentication) principal).isAuthenticated()) {
            return "forward:/";
        } else {
            return "login";
        }
    }

    @Override
    public String register(final Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @Override
    public String registerSuccessfully(final User user, final BindingResult bindingResult) {
        if (userNameExists(user.getUsername())) {
            bindingResult.addError(new FieldError("user", "username", "Username đã tồn tại trong hệ thống. Vui lòng thử lại."));
        }
        if (userEmailExists(user.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "Email đã tồn tại trong hệ thống. Vui lòng thử lại."));
        }
        if (bindingResult.hasErrors()) {
            return "register";
        } else {
            addUser(user);
            return "redirect:register?success";
        }
    }

    public void addUser(final User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        sendToken(user);
    }

    private void sendToken(final User user) {
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
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String verifyEmail(String value) {
        Token token = tokenRepository.findByValue(value);
        if (Objects.isNull(token)) {
            return "redirect:login?tokenError";
        }
        Optional<User> user = userRepository.findById(token.getUser().getId());
        if (Objects.isNull(user)) {
            return "redirect:login?tokenError";
        }

        User verifyUser = user.get();
        verifyUser.setEnabled(true);
        userRepository.save(verifyUser);

        tokenRepository.delete(token);
        return "redirect:login?tokenSuccess";
    }

    private boolean userEmailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    private boolean userNameExists(final String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public List<User> findUserByRole(String role){
        return  userRepository.findUserByRole(role);
    }

    @Override
    public List<User> findUserByUsernameOrEmail(String search, String role){
        return userRepository.findByUsernameContainingOrEmailContainingAndRole(search, search, role);
    }

    @Override
    public User get(Long id) throws UserNotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent())
            return result.get();
        else
            throw new UserNotFoundException("Không tìm thấy user với id = " +id);
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent())
            userRepository.deleteById(id);
        else
            throw new UserNotFoundException("Không tìm thấy user với id = " +id);

    }

    @Override
    public void lock(Long id) throws UserNotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()){
            User user = result.get();
            user.setLocked(true);
            userRepository.save(user);
        }
        else
            throw new UserNotFoundException("Không tìm thấy user với id = " +id);

    }

    @Override
    public void unlock(Long id) throws UserNotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()){
            User user = result.get();
            user.setLocked(false);
            userRepository.save(user);
        }
        else
            throw new UserNotFoundException("Không tìm thấy user với id = " +id);

    }

    @Override
    public void save(User user){
        userRepository.save(user);
    }
}
