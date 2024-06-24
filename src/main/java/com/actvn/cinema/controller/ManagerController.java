package com.actvn.cinema.controller;

import com.actvn.cinema.exception.ManagerNotFoundException;
import com.actvn.cinema.exception.UserNotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.model.Manager;
import com.actvn.cinema.model.User;
import com.actvn.cinema.service.BranchService;
import com.actvn.cinema.service.ManagerService;
import com.actvn.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;
    @Autowired
    private UserService userService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/search")
    public String search(@RequestParam("search") String search, Model model) {
        List<Manager> listManager = managerService.findByBranchNameContainingIgnoreCase(search);
        model.addAttribute("listManager", listManager);
        model.addAttribute("pageTitle", "Tìm kiếm quản lý");
        return "admin/management-manager";
    }

    @GetMapping("/new")
    public String add(Model model) {
        List<Branch> listBranch = branchService.listAll();
        model.addAttribute("listBranch", listBranch);
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
        } catch (ManagerNotFoundException managerNotFoundException) {
            ra.addFlashAttribute("errorMessage", managerNotFoundException.getMessage());
            return "redirect:/dashboard/management-manager";
        }
    }

    @PostMapping("/save")
    public String save(Manager manager, RedirectAttributes ra,
                       final BindingResult bindingResult, Model model) {
        User user = manager.getUser();
        user.setRole("ROLE_MANAGER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setLocked(false);
        userService.save(user);

        manager.setUser(user);
        managerService.save(manager);
        ra.addFlashAttribute("successMessage", "Lưu thông tin người quản lý rạp thành công.");
        return "redirect:/dashboard/management-manager";
    }

    @GetMapping("lock")
    public String lock(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            Manager manager = managerService.get(id);
            userService.lock(manager.getUser().getId());
            ra.addFlashAttribute("successMessage", "Khóa manager co ID = " + id + " thành công.");
        } catch (ManagerNotFoundException | UserNotFoundException managerNotFoundException) {
            ra.addFlashAttribute("errorMessage", managerNotFoundException.getMessage());
        }
        return "redirect:/dashboard/management-manager";
    }

    @GetMapping("unlock")
    public String unlock(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            Manager manager = managerService.get(id);
            userService.unlock(manager.getUser().getId());
            ra.addFlashAttribute("successMessage", "Mở khóa manager co ID = " + id + " thành công.");
        } catch (ManagerNotFoundException | UserNotFoundException managerNotFoundException) {
            ra.addFlashAttribute("errorMessage", managerNotFoundException.getMessage());
        }
        return "redirect:/dashboard/management-manager";
    }

    @GetMapping("delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            Manager manager = managerService.get(id);
            userService.delete(manager.getUser().getId());
            ra.addFlashAttribute("successMessage", "Xóa người quản lý có id = " + id + " thành công.");
        } catch (ManagerNotFoundException | UserNotFoundException managerNotFoundException) {
            ra.addFlashAttribute("errorMessage", managerNotFoundException.getMessage());
        }
        return "redirect:/dashboard/management-manager";
    }
}
