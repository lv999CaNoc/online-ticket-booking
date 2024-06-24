package com.actvn.cinema.controller;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.service.BranchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/branch")
public class BranchController {
    private BranchService branchService;

    @GetMapping("/search")
    public String search(@RequestParam("search") String search, Model model, RedirectAttributes ra) {
        try {
            List<Branch> branches = branchService.findBranchByNameContaining(search);
            model.addAttribute("branches", branches);
            model.addAttribute("pageTitle", "Tìm kiếm rạp");
            return "admin/management-branch";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-branch";
        }
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("branch", new Branch());
        model.addAttribute("pageTitle", "Thêm rạp mới");
        return "admin/branch-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Branch branch = branchService.get(id);
            model.addAttribute("branch", branch);
            model.addAttribute("pageTitle", "Cập nhật rạp " + branch.getName());
            return "admin/branch-form";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-branch";
        }
    }

    @PostMapping("/save")
    public String save(Branch branch, RedirectAttributes ra) {
        try {
            branchService.save(branch);
            ra.addFlashAttribute("successMessage", "Rạp " + branch.getName() + " đã lưu thành công.");
            return "redirect:/dashboard/management-branch";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/new";
        }
    }

    @GetMapping("delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            branchService.delete(id);
            ra.addFlashAttribute("successMessage", "Xóa rạp có id = " + id + " thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-branch";
    }
}
