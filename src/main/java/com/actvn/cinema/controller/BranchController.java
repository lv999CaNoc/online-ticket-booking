package com.actvn.cinema.controller;

import com.actvn.cinema.DTO.CityDTO;
import com.actvn.cinema.exception.BranchNotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.service.BranchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/branch")
public class BranchController {
    @Autowired private BranchService branchService;

    @GetMapping("/search")
    public String search(@RequestParam("search") String search
            , Model model, RedirectAttributes ra){
        try{
            List<Branch> branches = branchService.findBranchByNameContaining(search);
            model.addAttribute("branches", branches);
            model.addAttribute("pageTitle","Tìm kiếm rạp");
            return "admin/management-branch";
        }catch (BranchNotFoundException branchNotFoundException){
            ra.addFlashAttribute("errorMessage", branchNotFoundException.getMessage());
            return "redirect:/dashboard/management-branch";
        }
    }

    @GetMapping("/new")
    public String add(Model model){
        model.addAttribute("branch", new Branch());
        model.addAttribute("pageTitle", "Thêm rạp mới");
        return "admin/branch-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model,
                               RedirectAttributes ra){
        try {
            Branch branch = branchService.get(id);
            model.addAttribute("branch", branch);
            model.addAttribute("pageTitle", "Cập nhật rạp "+branch.getName());
            return "admin/branch-form";
        } catch (BranchNotFoundException branchNotFoundException) {
            ra.addFlashAttribute("errorMessage", branchNotFoundException.getMessage());
            return "redirect:/dashboard/management-branch";
        }
    }

    @PostMapping("/save")
    public String save(Branch branch, RedirectAttributes ra){
        branchService.save(branch);
        ra.addFlashAttribute("successMessage",
                "Rạp \""+branch.getName()+"\" đã lưu thành công.");
        return "redirect:/dashboard/management-branch";
    }

    @GetMapping("delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes ra){
        try {
            branchService.delete(id);
            ra.addFlashAttribute("successMessage",
                    "Xóa rạp có id = "+id+" thành công.");
        } catch (BranchNotFoundException branchNotFoundException) {
            ra.addFlashAttribute("errorMessage", branchNotFoundException.getMessage());
        }
        return "redirect:/dashboard/management-branch";
    }
}
