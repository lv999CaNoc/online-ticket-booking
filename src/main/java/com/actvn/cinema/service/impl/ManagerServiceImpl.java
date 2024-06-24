package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Manager;
import com.actvn.cinema.repositories.ManagerRepository;
import com.actvn.cinema.service.ManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Service
public class ManagerServiceImpl implements ManagerService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private Validator validator;

    @Override
    public List<Manager> listAll() throws NotFoundException {
        List<Manager> managers = managerRepository.findAll();
        if (managers.isEmpty())
            throw new NotFoundException("Không có Quản lý rạp");
        return managers;
    }

    @Override
    public void save(@Valid Manager manager) throws IllegalArgumentException {
        Errors errors = new BeanPropertyBindingResult(manager, "manager");
        validator.validate(manager, errors);
        if (errors.hasErrors()) {
            String message = errors.getAllErrors().get(0).getDefaultMessage();
            log.warn("[LOG] Validation errors: {}", message);
            throw new IllegalArgumentException(message);
        }
        managerRepository.save(manager);
        log.info("[LOG] Lưu quản lý {} thành công vào rap {}", manager.getUser().getUsername(), manager.getBranch().getName());
    }

    @Override
    public Manager get(Integer id) throws NotFoundException {
        Optional<Manager> result = managerRepository.findById(id);
        if (result.isPresent())
            return result.get();

        log.warn("[LOG] Không tìm thấy quản lý rạp có id: {}", id);
        throw new NotFoundException("Không tìm thấy quản lý rạp có id: " + id);
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        Optional<Manager> result = managerRepository.findById(id);
        if (result.isPresent()) {
            log.info("[LOG] Xoá thành công quản lý rạp với id: {}", id);
            managerRepository.deleteById(id);
        } else {
            log.warn("[LOG] Không tìm thấy quản lý rạp có id: {}", id);
            throw new NotFoundException("Không tìm thấy quản lý rạp có id: " + id);
        }
    }

    @Override
    public List<Manager> findByBranchNameContainingIgnoreCase(String keyword) throws NotFoundException {
        List<Manager> managers = managerRepository.findByBranchNameContainingIgnoreCase(keyword);
        if (managers.isEmpty()){
            log.warn("[LOG] Không tìm thấy chi nhánh có chứa keyword: {}", keyword);
            throw new NotFoundException("Không tìm thấy chi nhánh có chứa keyword: "+ keyword);
        }
        return managers;
    }
}
