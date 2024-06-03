package com.actvn.cinema.service;

import com.actvn.cinema.exception.ManagerNotFoundException;
import com.actvn.cinema.model.Manager;

import java.util.List;

public interface ManagerService {

    List<Manager> listAll();

    void save(Manager Manager);

    Manager get(Integer id) throws ManagerNotFoundException;

    void delete(Integer id) throws ManagerNotFoundException;

    List<Manager> findByBranchNameContainingIgnoreCase(String keyword);
}
