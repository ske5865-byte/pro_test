package com.example.portal.service;

import com.example.portal.domain.Member;
import com.example.portal.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberDetailsService memberDetailsService;

    @Test
    @DisplayName("Load user by email success")
    public void loadUserByUsername_success() {
        // Given
        String email = "test@example.com";
        Member member = new Member("testuser", "encodedPassword", email);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // When
        UserDetails userDetails = memberDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Load user by email fails when user not found")
    public void loadUserByUsername_notFound() {
        // Given
        String email = "notfound@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            memberDetailsService.loadUserByUsername(email);
        });
    }
}
