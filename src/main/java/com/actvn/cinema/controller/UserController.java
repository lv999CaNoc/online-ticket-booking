package com.actvn.cinema.controller;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.User;
import com.actvn.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/search")
    public String searchByUsernameOrEmail(@RequestParam("search") String search, Model model) {
        List<User> userByUsernameOrEmail = userService.findUserByUsernameOrEmail(search, "ROLE_USER");
        model.addAttribute("listUser", userByUsernameOrEmail);
        model.addAttribute("pageTitle", "Tìm kiếm " + search);
        model.addAttribute("successMessage", "Tìm thấy " + userByUsernameOrEmail.size() + " người dùng.");
        return "admin/management-user";
    }

    @GetMapping("/user/lock")
    public String lock(@RequestParam("id") Long id, RedirectAttributes ra) {
        try {
            userService.lock(id);
            ra.addFlashAttribute("successMessage",
                    "Khóa user có id = " + id + " thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-user";
    }

    @GetMapping("/user/unlock")
    public String unlock(@RequestParam("id") Long id, RedirectAttributes ra) {
        try {
            userService.unlock(id);
            ra.addFlashAttribute("successMessage",
                    "Mở khóa user có id = " + id + " thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-user";
    }
}
