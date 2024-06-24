package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.repositories.BranchRepository;
import com.actvn.cinema.service.BranchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {
    private static final Logger log = LoggerFactory.getLogger(BranchServiceImpl.class);

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private Validator validator;

    @Override
    public List<Branch> findBranchByNameContaining(String name) throws NotFoundException {
        List<Branch> branchesByName = branchRepository.findBranchByNameContainingIgnoreCase(name);
        if (branchesByName.isEmpty()) {
            log.warn("[LOG] Không tìm thấy rạp có tên chứa {}", name);
            throw new NotFoundException("Không tìm thấy rạp có tên chứa " + name);
        }
        return branchesByName;
    }

    @Override
    public List<Branch> listAll() throws NotFoundException {
        List<Branch> branches = branchRepository.findAll();
        if (branches.isEmpty()) {
            log.warn("[LOG] Không có chi nhánh");
            throw new NotFoundException("Không có chi nhánh");
        }
        return branches;
    }

    @Override
    public void save(Branch branch) throws IllegalArgumentException {
        Errors errors = new BeanPropertyBindingResult(branch, "branch");
        validator.validate(branch, errors);

        if (errors.hasErrors()) {
            String message = errors.getAllErrors().get(0).getDefaultMessage();
            log.warn("[LOG] Validation errors: {}", message);
            throw new IllegalArgumentException(errors.getAllErrors().get(0).getDefaultMessage());
        }
        branchRepository.save(branch);
        log.info("[LOG] Lưu chi nhánh thành công");
    }

    @Override
    public Branch get(Integer id) throws NotFoundException {
        Optional<Branch> result = branchRepository.findById(id);
        if (result.isPresent())
            return result.get();
        log.warn("[LOG] Không tìm thấy rạp với id: {}", id);
        throw new NotFoundException("Không tìm thấy rạp với id: " + id);
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        Optional<Branch> result = branchRepository.findById(id);
        if (result.isPresent()) {
            branchRepository.deleteById(id);
            log.info("[LOG] Xoá chi nhánh thành công");
        } else {
            log.warn("[LOG] Không tìm thấy rạp với id: {}", id);
            throw new NotFoundException("Không tìm thấy rạp với id = " + id);
        }
    }


}
