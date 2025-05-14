package com.hibi.server.domain.member.controller;

import com.hibi.server.domain.member.dto.MemberResponse; // 실제 DTO 경로에 맞게 수정 필요
import com.hibi.server.domain.member.dto.MemberSignInRequest; // 실제 DTO 경로에 맞게 수정 필요
import com.hibi.server.domain.member.dto.MemberSignUpRequest; // 실제 DTO 경로에 맞게 수정 필요
import com.hibi.server.domain.member.dto.MemberUpdateRequest; // 실제 DTO 경로에 맞게 수정 필요
// import com.hibi.server.global.common.ApiResponse; // 공통 응답 DTO가 있다면 사용
// import com.hibi.server.domain.auth.dto.AuthResponse; // 인증 응답 DTO 경로
// import com.hibi.server.domain.member.service.MemberService; // 서비스 인터페이스 경로

import com.hibi.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService; // 실제 서비스 주입

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공 (임시 응답)");
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody MemberSignInRequest request) {
        return ResponseEntity.ok("로그인 성공 (임시 응답)");
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut() {
        return ResponseEntity.ok("로그아웃 성공 (임시 응답)");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(/* @AuthenticationPrincipal UserDetails userDetails */) {
        // 임시 응답
        return ResponseEntity.ok("내 정보 조회 성공 (임시 응답)");
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyInfo(/* @AuthenticationPrincipal UserDetails userDetails, */
                                       @Valid @RequestBody MemberUpdateRequest request) {
        // 임시 응답
        return ResponseEntity.ok("내 정보 수정 성공 (임시 응답)");
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> withdrawMember(/* @AuthenticationPrincipal UserDetails userDetails */) {
        // 임시 응답
        return ResponseEntity.ok("회원 탈퇴 성공 (임시 응답)");
    }

    @GetMapping("/{memberId}")
    // @PreAuthorize("hasRole('ADMIN')") // Spring Security 사용 시 권한 설정
    public ResponseEntity<?> getMemberById(@PathVariable Long memberId) {
        // 임시 응답
        return ResponseEntity.ok(memberId + "번 회원 정보 조회 성공 (임시 응답)");
    }

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')") // Spring Security 사용 시 권한 설정
    public ResponseEntity<?> getAllMembers() {
        // 임시 응답
        return ResponseEntity.ok("모든 회원 정보 조회 성공 (임시 응답)");
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        // 임시 응답
        return ResponseEntity.ok(email + " 이메일 사용 가능 여부 확인 (임시 응답)");
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNicknameAvailability(@RequestParam String nickname) {
        // 임시 응답
        return ResponseEntity.ok(nickname + " 닉네임 사용 가능 여부 확인 (임시 응답)");
    }

}