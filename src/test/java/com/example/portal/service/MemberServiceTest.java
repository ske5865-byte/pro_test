package com.example.portal.service;

import com.example.portal.domain.Member;
import com.example.portal.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.lang.reflect.Field;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("Join success")
    public void join_success() {
        // Given
        when(memberRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // When
        // Simulate the save operation setting an ID on the member object
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member memberToSave = invocation.getArgument(0);
            // Use reflection to set the private ID field for the test
            try {
                Field idField = Member.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(memberToSave, 1L); // Assign a dummy ID
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return memberToSave;
        });

        Long memberId = memberService.join("testuser", "password", "test@example.com");

        // Then
        assertNotNull(memberId);
        assertEquals(1L, memberId);
    }

    @Test
    @DisplayName("Join fails with duplicate username")
    public void join_fail_duplicateUsername() {
        // Given
        when(memberRepository.findByUsername("testuser")).thenReturn(Optional.of(new Member("testuser", "p", "e")));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join("testuser", "password", "test@example.com");
        });
    }

    @Test
    @DisplayName("Join fails with duplicate email")
    public void join_fail_duplicateEmail() {
        // Given
        when(memberRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Member("u", "p", "test@example.com")));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join("testuser", "password", "test@example.com");
        });
    }
}
