package com.example.demo.service;

import com.example.demo.domain.Post;
import com.example.demo.domain.User;
import com.example.demo.dto.PostCreateRequestDto;
import com.example.demo.dto.PostUpdateRequestDto;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long create(PostCreateRequestDto requestDto, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = new Post(requestDto.getTitle(), requestDto.getContent(), author);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }

    @Transactional
    public void update(Long id, PostUpdateRequestDto requestDto, String username) {
        Post post = findById(id);
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to edit this post");
        }
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        // The transaction will commit the changes
    }

    @Transactional
    public void delete(Long id, String username) {
        Post post = findById(id);
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to delete this post");
        }
        postRepository.delete(post);
    }
}
