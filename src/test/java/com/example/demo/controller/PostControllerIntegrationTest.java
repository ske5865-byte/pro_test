package com.example.demo.controller;

import com.example.demo.domain.Post;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // We need to manage test data manually because @Transactional interferes with @WithUserDetails
    @BeforeEach
    public void setup() {
        User testUser = new User("testauthor", passwordEncoder.encode("password"), "author@example.com", Role.USER);
        userRepository.save(testUser);
    }

    @AfterEach
    public void cleanup() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Anonymous user can view the post list")
    public void anonymousUser_canViewPostList() throws Exception {
        // Given a post exists
        User author = userRepository.findByUsername("testauthor").get();
        postRepository.save(new Post("A Post", "Some content", author));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("post-list"))
                .andExpect(content().string(containsString("Bulletin Board")));
    }

    @Test
    @DisplayName("Anonymous user is redirected to login when trying to create a post")
    public void anonymousUser_redirectToLogin_forNewPost() throws Exception {
        mockMvc.perform(get("/posts/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("Authenticated user can create, edit, and delete their own post")
    @WithUserDetails("testauthor")
    public void authenticatedUser_canManageOwnPost() throws Exception {
        // 1. Create Post
        mockMvc.perform(post("/posts/new")
                        .param("title", "My First Post")
                        .param("content", "This is the content.")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        Post newPost = postRepository.findAll().get(0);
        Long postId = newPost.getId();

        // 2. View Post
        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("My First Post")));

        // 3. Edit Post
        mockMvc.perform(post("/posts/" + postId + "/edit")
                        .param("title", "Updated Title")
                        .param("content", "Updated content.")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));

        // 4. Delete Post
        mockMvc.perform(post("/posts/" + postId + "/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    @DisplayName("User cannot edit or delete another user's post")
    @WithUserDetails("testauthor")
    public void user_cannotModifyOthersPost() throws Exception {
        // Create a post by another user
        User otherUser = new User("otheruser", passwordEncoder.encode("password"), "other@example.com", Role.USER);
        userRepository.save(otherUser);
        Post otherPost = new Post("Other's Post", "Content", otherUser);
        postRepository.save(otherPost);
        Long otherPostId = otherPost.getId();

        // Try to access edit page (should fail)
        try {
            mockMvc.perform(get("/posts/" + otherPostId + "/edit"));
        } catch (NestedServletException e) {
            assertTrue(e.getCause() instanceof org.springframework.security.access.AccessDeniedException);
        }

        // Try to submit an edit (should fail)
        try {
            mockMvc.perform(post("/posts/" + otherPostId + "/edit")
                            .param("title", "Hacked Title")
                            .with(csrf()));
        } catch (NestedServletException e) {
            assertTrue(e.getCause() instanceof org.springframework.security.access.AccessDeniedException);
        }


        // Try to delete (should fail)
        try {
            mockMvc.perform(post("/posts/" + otherPostId + "/delete")
                            .with(csrf()));
        } catch (NestedServletException e) {
            assertTrue(e.getCause() instanceof org.springframework.security.access.AccessDeniedException);
        }
    }
}
