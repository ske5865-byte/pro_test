package com.example.portal.controller;

import com.example.portal.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String passwordConfirm,
            @RequestParam String email,
            @RequestParam(required = false) boolean agreement,
            Model model) {

        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        if (!agreement) {
            model.addAttribute("error", "You must agree to the terms.");
            return "register";
        }

        try {
            memberService.join(username, password, email);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        return "redirect:/login?success";
    }

    // API endpoints for AJAX validation

    @GetMapping("/api/members/check-username")
    @ResponseBody
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        return Map.of("isDuplicate", memberService.isUsernameDuplicate(username));
    }

    @GetMapping("/api/members/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        return Map.of("isDuplicate", memberService.isEmailDuplicate(email));
    }
}
