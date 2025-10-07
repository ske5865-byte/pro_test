package com.example.demo.controller;

import com.example.demo.dto.UserJoinRequestDto;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userJoinRequestDto", new UserJoinRequestDto("", "", ""));
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute UserJoinRequestDto userJoinRequestDto) {
        try {
            userService.join(userJoinRequestDto);
        } catch (IllegalArgumentException e) {
            // In a real application, you'd want to return this error to the user
            return "redirect:/signup?error";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
