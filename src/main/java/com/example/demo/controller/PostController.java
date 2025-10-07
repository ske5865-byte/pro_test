package com.example.demo.controller;

import com.example.demo.domain.Post;
import com.example.demo.dto.PostCreateRequestDto;
import com.example.demo.dto.PostUpdateRequestDto;
import com.example.demo.service.PostService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String list(Model model) {
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "post-list";
    }

    @GetMapping("/new")
    public String newPost(Model model) {
        model.addAttribute("postCreateRequestDto", new PostCreateRequestDto());
        return "post-form";
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute PostCreateRequestDto requestDto, Principal principal) {
        postService.create(requestDto, principal.getName());
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "post-detail";
    }

    @GetMapping("/{id}/edit")
    public String editPost(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.findById(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("You do not have permission to edit this post");
        }
        model.addAttribute("post", post);
        model.addAttribute("postUpdateRequestDto", new PostUpdateRequestDto(post.getTitle(), post.getContent()));
        return "post-form";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute PostUpdateRequestDto requestDto, Principal principal) {
        postService.update(id, requestDto, principal.getName());
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, Principal principal) {
        postService.delete(id, principal.getName());
        return "redirect:/posts";
    }
}
