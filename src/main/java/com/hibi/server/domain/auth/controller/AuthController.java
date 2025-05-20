package com.hibi.server.domain.auth.controller;

import com.hibi.server.domain.auth.dto.request.SignUpRequest;
import com.hibi.server.domain.member.dto.MemberSignUpRequest;
import com.hibi.server.domain.member.service.MemberService;
import com.hibi.server.global.common.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(memberService.signUp(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(memberService.signIn(request));
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(@RequestParam Long memberId) {
        memberService.signOut(memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email-check")
    public ResponseEntity<Boolean> checkEmailAvailability(@RequestParam String email) {
        return ResponseEntity.ok(memberService.isEmailAvailable(email));
    }

    @GetMapping("/nickname-check")
    public ResponseEntity<Boolean> checkNicknameAvailability(@RequestParam String nickname) {
        return ResponseEntity.ok(memberService.isNicknameAvailable(nickname));
    }
}