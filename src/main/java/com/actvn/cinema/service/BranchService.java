package com.actvn.cinema.service;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Branch;

import java.util.List;

public interface BranchService {

    List<Branch> findBranchByNameContaining(String name) throws NotFoundException;

    List<Branch> listAll() throws NotFoundException;

    void save(Branch branch) throws IllegalArgumentException;

    Branch get(Integer id) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;

}
