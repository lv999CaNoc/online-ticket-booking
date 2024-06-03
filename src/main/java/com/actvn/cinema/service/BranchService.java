package com.actvn.cinema.service;

import com.actvn.cinema.DTO.ResponseSearchScheduleDTO;
import com.actvn.cinema.DTO.SearchScheduleDTO;
import com.actvn.cinema.exception.BranchNotFoundException;
import com.actvn.cinema.model.Branch;

import java.util.List;

public interface BranchService {
    List<Branch> getBranchThatShowTheMovie(Integer movieId);

    List<Branch> findBranchByNameContaining(String name) throws BranchNotFoundException;

    List<Branch> findBranchByCity(String city);

    List<Branch> listAll();

    void save(Branch branch);

    Branch get(Integer id) throws BranchNotFoundException;

    void delete(Integer id) throws BranchNotFoundException;

}
