package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.BranchNotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.repositories.BranchRepository;
import com.actvn.cinema.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public List<Branch> getBranchThatShowTheMovie(Integer movieId){
        return branchRepository.getBranchThatShowTheMovie(movieId);
    }

    @Override
    public List<Branch> findBranchByNameContaining(String name) throws BranchNotFoundException{
        List<Branch> branchByName = branchRepository.findBranchByNameContainingIgnoreCase(name);
        if (branchByName.isEmpty()) {
            throw new BranchNotFoundException("Không tìm thấy rạp có tên chứa \""+name+"\"");
        }
        return branchByName;
    }

    @Override
    public List<Branch> findBranchByCity(String city){
        return branchRepository.findBranchByThanhPho(city);
    }

    @Override
    public List<Branch> listAll(){
        return (List<Branch>) branchRepository.findAll();
    }
    @Override
    public void save(Branch branch){
         branchRepository.save(branch);
    }
    @Override
    public Branch get(Integer id) throws BranchNotFoundException {
        Optional<Branch> result =branchRepository.findById(id);
        if (result.isPresent())
            return result.get();
        throw new BranchNotFoundException("Không tìm thấy rạp với id = " +id);
    }
    @Override
    public void delete(Integer id) throws BranchNotFoundException {
        Optional<Branch> result = branchRepository.findById(id);
        if (result.isPresent())
            branchRepository.deleteById(id);
        else
            throw new BranchNotFoundException("Không tìm thấy rạp với id = " +id);

    }


}
