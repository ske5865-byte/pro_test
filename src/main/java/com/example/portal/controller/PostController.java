package com.example.portal.controller;

import com.example.portal.domain.Post;
import com.example.portal.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/board")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Post> posts = postService.findPosts(pageable);
        model.addAttribute("posts", posts);
        return "boardList";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "boardDetail";
    }

    @GetMapping("/write")
    public String writeForm() {
        return "boardWrite";
    }

    @PostMapping("/write")
    public String write(@RequestParam String title, @RequestParam String content, Principal principal) {
        postService.save(title, content, principal.getName());
        return "redirect:/board/list";
    }
}
