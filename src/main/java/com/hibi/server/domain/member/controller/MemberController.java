package com.hibi.server.domain.member.controller;

import com.hibi.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService; // 실제 서비스 주입


    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(/* @AuthenticationPrincipal UserDetails userDetails */) {
        // 임시 응답
        return ResponseEntity.ok("내 정보 조회 성공 (임시 응답)");
    }

//    @PutMapping("/me")
//    public ResponseEntity<?> updateMyInfo(/* @AuthenticationPrincipal UserDetails userDetails, */
//            @Valid @RequestBody MemberUpdateRequest request) {
//        // 임시 응답
//        return ResponseEntity.ok("내 정보 수정 성공 (임시 응답)");
//    }

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