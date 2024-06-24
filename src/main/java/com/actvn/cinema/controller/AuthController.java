package com.actvn.cinema.controller;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.User;
import com.actvn.cinema.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @GetMapping("/login")
    public String login(final Principal principal) {
        if (authService.login(principal))
            return "forward:/";
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user,
                           RedirectAttributes ra) {
        try {
            authService.register(user);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
        return "redirect:register?success";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmail(token);
        } catch (NotFoundException e) {
            return "redirect:login?tokenError";
        }
        return "redirect:login?success";
    }
}
