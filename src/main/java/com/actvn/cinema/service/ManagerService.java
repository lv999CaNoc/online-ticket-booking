package com.actvn.cinema.service;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Manager;

import java.util.List;

public interface ManagerService {

    List<Manager> listAll() throws NotFoundException;

    void save(Manager Manager) throws IllegalArgumentException;

    Manager get(Integer id) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;

    List<Manager> findByBranchNameContainingIgnoreCase(String keyword) throws NotFoundException;
}
