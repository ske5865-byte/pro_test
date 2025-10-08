package com.example.portal.controller;

import com.example.portal.domain.Member;
import com.example.portal.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        // Create a user for login tests
        Member testUser = new Member("testuser", passwordEncoder.encode("password123"), "test@example.com");
        memberRepository.save(testUser);
    }

    @AfterEach
    public void cleanup() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Login page is rendered correctly")
    public void loginPage_isRendered() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("Login with valid credentials succeeds and redirects to home")
    public void login_success() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Login with invalid credentials fails and redirects to login with error")
    public void login_fail_invalidCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "test@example.com")
                        .param("password", "wrongpassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @DisplayName("Unauthenticated user is redirected to login page when accessing a protected route")
    public void unauthenticatedUser_redirectToLogin_forProtectedPage() throws Exception {
        mockMvc.perform(get("/board/write"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
