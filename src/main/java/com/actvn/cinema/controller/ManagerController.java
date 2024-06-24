package com.actvn.cinema.controller;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.model.Manager;
import com.actvn.cinema.model.User;
import com.actvn.cinema.service.BranchService;
import com.actvn.cinema.service.ManagerService;
import com.actvn.cinema.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/manager")
public class ManagerController {

    private ManagerService managerService;
    private UserService userService;
    private BranchService branchService;
    private PasswordEncoder passwordEncoder;

    @GetMapping("/search")
    public String search(@RequestParam("search") String search, Model model) {
        try {
            List<Manager> listManager = managerService.findByBranchNameContainingIgnoreCase(search);
            model.addAttribute("listManager", listManager);
        } catch (NotFoundException e) {
            model.addAttribute("errorEmpty", e.getMessage());
        }
        model.addAttribute("pageTitle", "Tìm kiếm quản lý");
        return "admin/management-manager";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        try {
            List<Branch> listBranch = branchService.listAll();
            model.addAttribute("listBranch", listBranch);
        } catch (NotFoundException e) {
            model.addAttribute("errorBranchEmpty", e.getMessage());
        }
        model.addAttribute("manager", new Manager());
        model.addAttribute("pageTitle", "Thêm người quản lý rạp mới");
        return "admin/manager-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Manager manager = managerService.get(id);
            List<Branch> listBranch = branchService.listAll();
            model.addAttribute("listBranch", listBranch);
            model.addAttribute("manager", manager);
            model.addAttribute("pageTitle", "Cập nhật người quản lý rạp");
            return "admin/manager-form";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-manager";
        }
    }

    @PostMapping("/save")
    public String save(Manager manager, RedirectAttributes ra) {
        try {
            User user = manager.getUser();
            user.setRole("ROLE_MANAGER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);
            user.setLocked(false);
            if (manager.getId() != null){
                userService.update(user);
            }else{
                userService.save(user);
            }

            manager.setUser(user);
            managerService.save(manager);
            ra.addFlashAttribute("successMessage", "Lưu thông tin người quản lý rạp thành công.");
        } catch (IllegalArgumentException |NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-manager";
    }

    @GetMapping("lock")
    public String lock(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            Manager manager = managerService.get(id);
            User user = manager.getUser();
            userService.lock(user.getId());
            ra.addFlashAttribute("successMessage", "Khóa manager có username: " + user.getUsername() + " thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-manager";
    }

    @GetMapping("unlock")
    public String unlock(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            Manager manager = managerService.get(id);
            User user = manager.getUser();
            userService.unlock(user.getId());
            ra.addFlashAttribute("successMessage", "Mở khoá manager có username:  " + user.getUsername() + " thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-manager";
    }

    @GetMapping("delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            Manager manager = managerService.get(id);
            User user = manager.getUser();
            userService.delete(user.getId());
            ra.addFlashAttribute("successMessage", "Xoá manager có username:  " + user.getUsername() + " thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-manager";
    }
}
