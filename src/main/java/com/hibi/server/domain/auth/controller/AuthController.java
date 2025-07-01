package com.hibi.server.domain.auth.controller;

import com.hibi.server.domain.auth.dto.request.SignInRequest;
import com.hibi.server.domain.auth.dto.request.SignUpRequest;
import com.hibi.server.domain.auth.dto.response.ReissueResponse;
import com.hibi.server.domain.auth.dto.response.SignInResponse;
import com.hibi.server.domain.auth.service.AuthService;
import com.hibi.server.domain.auth.service.RefreshTokenService;
import com.hibi.server.domain.member.dto.response.AvailabilityResponse;
import com.hibi.server.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.BiFunction;

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

    @Operation(
            summary = "이메일 사용 가능 여부 확인",
            description = "주어진 이메일이 현재 사용 가능한지 확인합니다."
    )
    @GetMapping("/check-email")
    public ResponseEntity<SuccessResponse<AvailabilityResponse>> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = authService.checkEmailAvailability(email);
        return buildAvailabilityResponse(email, isAvailable, AvailabilityResponse::forEmail);
    }

    @Operation(
            summary = "닉네임 사용 가능 여부 확인",
            description = "주어진 닉네임이 현재 사용 가능한지 확인합니다."
    )
    @GetMapping("/check-nickname")
    public ResponseEntity<SuccessResponse<AvailabilityResponse>> checkNicknameAvailability(@RequestParam String nickname) {
        boolean isAvailable = authService.checkNicknameAvailability(nickname);
        return buildAvailabilityResponse(nickname, isAvailable, AvailabilityResponse::forNickname);
    }

    private ResponseEntity<SuccessResponse<AvailabilityResponse>> buildAvailabilityResponse(
            String target,
            boolean isAvailable,
            BiFunction<String, Boolean, AvailabilityResponse> responseFactory
    ) {
        String message = isAvailable
                ? target + "은(는) 사용 가능합니다."
                : target + "은(는) 이미 사용 중입니다.";

        AvailabilityResponse response = responseFactory.apply(target, isAvailable);
        return ResponseEntity.ok(SuccessResponse.success(message, response));
    }
}