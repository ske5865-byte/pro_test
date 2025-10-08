package com.example.portal.service;

import com.example.portal.domain.Member;
import com.example.portal.domain.Post;
import com.example.portal.repository.MemberRepository;
import com.example.portal.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Long save(String title, String content, String username) {
        Member author = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + username));
        Post post = new Post(title, content, author);
        postRepository.save(post);
        return post.getId();
    }

    public Page<Post> findPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }
}
