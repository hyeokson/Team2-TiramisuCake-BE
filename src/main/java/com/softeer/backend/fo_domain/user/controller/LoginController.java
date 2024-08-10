package com.softeer.backend.fo_domain.user.controller;

import com.softeer.backend.fo_domain.user.dto.LoginRequestDto;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.fo_domain.user.service.LoginService;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    ResponseDto<JwtTokenResponseDto> handleLogin(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        JwtTokenResponseDto jwtTokenResponseDto = loginService.handleLogin(loginRequestDto);

        return ResponseDto.onSuccess(jwtTokenResponseDto);
    }

}
