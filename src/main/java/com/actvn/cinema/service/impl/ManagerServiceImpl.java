package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.ManagerNotFoundException;
import com.actvn.cinema.model.Manager;
import com.actvn.cinema.repositories.ManagerRepository;
import com.actvn.cinema.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public List<Manager> listAll(){
        return (List<Manager>) managerRepository.findAll();
    }
    @Override
    public void save(Manager manager){
        managerRepository.save(manager);
    }
    @Override
    public Manager get(Integer id) throws ManagerNotFoundException {
        Optional<Manager> result = managerRepository.findById(id);
        if (result.isPresent())
            return result.get();
        throw new ManagerNotFoundException("Không tìm thấy quản lý rạp có id = " +id);
    }
    @Override
    public void delete(Integer id) throws ManagerNotFoundException {
        Optional<Manager> result = managerRepository.findById(id);
        if (result.isPresent())
            managerRepository.deleteById(id);
        else
            throw new ManagerNotFoundException("Không tìm thấy quản lý rạp có id = " +id);
    }

    @Override
    public List<Manager> findByBranchNameContainingIgnoreCase(String keyword){
        return managerRepository.findByBranchNameContainingIgnoreCase(keyword);
    }
}
