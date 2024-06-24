package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.User;
import com.actvn.cinema.repositories.UserRepository;
import com.actvn.cinema.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Validator validator;

    @Override
    public User getUserByEmail(final String email) throws NotFoundException {
        User user = userRepository.findByEmail(email);
        if (Objects.isNull(user)) {
            log.warn("[LOG] User not found with email: {}", email);
            throw new NotFoundException("User not found with email");
        }
        return user;
    }

    @Override
    public User getUserByUsername(final String username) throws NotFoundException {
        User user = userRepository.findByUsername(username);
        if (Objects.isNull(user)) {
            log.warn("[LOG] User not found with username: {}", username);
            throw new NotFoundException("User not found with username");
        }
        return user;
    }

    @Override
    public boolean userEmailExists(String email) {
        return !Objects.isNull(userRepository.findByEmail(email));
    }

    @Override
    public boolean userUsernameExists(String username) {
        return !Objects.isNull(userRepository.findByUsername(username));
    }

    @Override
    public List<User> findUserByRole(String role) throws NotFoundException {
        List<User> userByRole = userRepository.findUserByRole(role);
        if (userByRole.isEmpty()){
            throw new NotFoundException("Không có Người dùng có vai trò: "+role);
        }
        return userByRole;
    }

    @Override
    public List<User> findUserByUsernameOrEmail(String search, String role) {
        return userRepository.findByUsernameOrEmailContainingAndRole(search, search, role);
    }

    @Override
    public User get(Long id) throws NotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent())
            return result.get();
        else {
            log.warn("[LOG] Không tìm thấy user với id: {}", id);
            throw new NotFoundException("Không tìm thấy user với id: " + id);
        }
    }

    @Override
    public void delete(Long id) throws NotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()) {
            userRepository.deleteById(id);
            log.info("[LOG] Xoá thành công user với id: {}", id);
        } else {
            log.warn("[LOG] Không tìm thấy user với id: {}", id);
            throw new NotFoundException("Không tìm thấy user với id = " + id);
        }
    }

    @Override
    public void lock(Long id) throws NotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()) {
            User user = result.get();
            user.setLocked(true);
            log.info("[LOG] Khoá thành công user với id: {}", id);
            userRepository.save(user);
        } else {
            log.warn("[LOG] Không tìm thấy user với id: {}", id);
            throw new NotFoundException("Không tìm thấy user với id = " + id);
        }
    }

    @Override
    public void unlock(Long id) throws NotFoundException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()) {
            User user = result.get();
            user.setLocked(false);
            log.info("[LOG] Mở khoá thành công user với id: {}", id);
            userRepository.save(user);
        } else {
            log.warn("[LOG] Không tìm thấy user với id: {}", id);
            throw new NotFoundException("Không tìm thấy user với id = " + id);
        }
    }

    @Override
    public void save(@Valid User user) throws IllegalArgumentException {
        Errors errors = new BeanPropertyBindingResult(user, "user");

        if (userEmailExists(user.getEmail())) {
            errors.rejectValue("username", "username.exists", "Username đã tồn tại trong hệ thống. Vui lòng thử lại.");
        }
        if (userUsernameExists(user.getEmail())) {
            errors.rejectValue("email", "email.exists", "Email đã tồn tại trong hệ thống. Vui lòng thử lại.");
        }

        validator.validate(user, errors);
        if (errors.hasErrors()) {
            String message = errors.getAllErrors().get(0).getDefaultMessage();
            log.warn("[LOG] Validation errors: {}", message);
            throw new IllegalArgumentException(message);
        }
        log.info("[LOG] Lưu người dùng với username {} thành công.", user.getUsername());
        userRepository.save(user);
    }

    @Override
    public void update(@Valid User user) throws IllegalArgumentException, NotFoundException {
        Errors errors = new BeanPropertyBindingResult(user, "user");

        if (getUserByEmail(user.getEmail()).getId() != user.getId()) {
            errors.rejectValue("username", "username.exists", "Username đã tồn tại trong hệ thống. Vui lòng thử lại.");
        }
        if (getUserByUsername(user.getUsername()).getId() != user.getId()) {
            errors.rejectValue("email", "email.exists", "Email đã tồn tại trong hệ thống. Vui lòng thử lại.");
        }

        validator.validate(user, errors);
        if (errors.hasErrors()) {
            String message = errors.getAllErrors().get(0).getDefaultMessage();
            log.warn("[LOG] Validation errors: {}", message);
            throw new IllegalArgumentException(message);
        }
        log.info("[LOG] Cập nhật người dùng với username {} thành công.", user.getUsername());
        userRepository.save(user);
    }
}
