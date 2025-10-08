package com.example.portal.service;

import com.example.portal.domain.Member;
import com.example.portal.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long join(String username, String password, String email) {
        validateDuplicateMember(username, email);
        Member member = new Member(username, passwordEncoder.encode(password), email);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(String username, String email) {
        if (isUsernameDuplicate(username)) {
            throw new IllegalStateException("This username is already taken.");
        }
        if (isEmailDuplicate(email)) {
            throw new IllegalStateException("This email is already in use.");
        }
    }

    public boolean isUsernameDuplicate(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
