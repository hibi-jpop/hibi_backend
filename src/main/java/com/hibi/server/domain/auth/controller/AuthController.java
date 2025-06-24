package com.hibi.server.domain.auth.controller;

import com.hibi.server.domain.auth.dto.request.SignInRequest;
import com.hibi.server.domain.auth.dto.request.SignUpRequest;
import com.hibi.server.domain.auth.dto.response.ReissueResponse;
import com.hibi.server.domain.auth.dto.response.SignInResponse;
import com.hibi.server.domain.auth.service.AuthService;
import com.hibi.server.domain.auth.service.RefreshTokenService;
import com.hibi.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/sign-up")
    public ResponseEntity<SuccessResponse<?>> signup(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(SuccessResponse.success("회원가입에 성공했습니다."));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SuccessResponse<SignInResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(SuccessResponse.success("로그인에 성공하였습니다.", authService.signIn(request)));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<SuccessResponse<?>> signOut(@RequestParam Long memberId) {
        authService.signOut(memberId);
        return ResponseEntity.ok(SuccessResponse.success("로그아웃하였습니다."));
    }
    
    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse<ReissueResponse>> reissueTokens(@RequestParam String refreshToken) {
        return ResponseEntity.ok(SuccessResponse.success("토큰 재발급에 성공하였습니다.", refreshTokenService.reissueTokens(refreshToken)));
    }
}