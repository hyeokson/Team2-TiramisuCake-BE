package com.softeer.backend.global.util;

import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RoleType;
import com.softeer.backend.global.common.entity.AuthInfo;
import com.softeer.backend.global.common.exception.JwtAuthenticationException;
import com.softeer.backend.global.config.properties.JwtProperties;
import com.softeer.backend.fo_domain.user.dto.UserTokenResponse;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;

    // HttpServletRequest 부터 Access Token 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getAccessHeader()))
                .filter(StringUtils::hasText)
                .filter(accessToken -> accessToken.startsWith(jwtProperties.getBearer()))
                .map(accessToken -> accessToken.substring(jwtProperties.getBearer().length()+1));
    }

    // HttpServletRequest 부터 Refresh Token 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getRefreshHeader()));
    }

    // access token 생성
    public String createAccessToken(AuthInfo authInfo) {
        return this.createToken(authInfo, jwtProperties.getAccessExpiration());
    }

    // refresh token 생성
    public String createRefreshToken(AuthInfo authInfo) {
        return this.createToken(authInfo, jwtProperties.getRefreshExpiration());

    }

    // access token 으로부터 인증 정보 추출
    public AuthInfo getAuthInfoFromAccessToken(String token) {
        try {

            return getAuthInfoFromToken(token);

        } catch (Exception exception) {
            throw new JwtAuthenticationException(ErrorStatus._JWT_ACCESS_TOKEN_IS_NOT_VALID);
        }
    }

    // refresh token 으로부터 인증 정보 추출
    public AuthInfo getAuthInfoFromRefreshToken(String token) {
        try {

            return getAuthInfoFromToken(token);

        } catch (Exception exception) {
            throw new JwtAuthenticationException(ErrorStatus._JWT_REFRESH_TOKEN_IS_NOT_VALID);
        }
    }

    // Jwt Token 에서 AuthInfo 파싱하여 반환하는 메서드
    private AuthInfo getAuthInfoFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();

        int id = Integer.parseInt(claims.get("id", String.class));
        RoleType roleType = RoleType.valueOf(claims.get("roleType", String.class));

        return AuthInfo.builder()
                .id(id)
                .roleType(roleType)
                .build();
    }

    // 전화번호 로그인 및 admin 로그인 시 jwt 응답 생성 + redis refresh 저장
    public UserTokenResponse createServiceToken(AuthInfo authInfo) {
        redisUtil.deleteData(redisUtil.getRedisKeyForJwt(authInfo));
        String accessToken = createAccessToken(authInfo);
        String refreshToken = createRefreshToken(authInfo);

        // 서비스 토큰 생성
        UserTokenResponse userTokenResponse = UserTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredTime(LocalDateTime.now().plusSeconds(jwtProperties.getAccessExpiration() / 1000))
                .build();

        // redis refresh token 저장
        redisUtil.setDataExpire(redisUtil.getRedisKeyForJwt(authInfo),
                userTokenResponse.getRefreshToken(), jwtProperties.getRefreshExpiration());

        return userTokenResponse;
    }

    // token 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException exception) {
            log.warn("만료된 jwt 입니다.");
        } catch (UnsupportedJwtException exception) {
            log.warn("지원되지 않는 jwt 입니다.");
        } catch (IllegalArgumentException exception) {
            log.warn("token에 값이 없습니다.");
        } catch(SignatureException exception){
            log.warn("signature에 오류가 존재합니다.");
        } catch(MalformedJwtException exception){
            log.warn("jwt가 유효하지 않습니다.");
        }
        return false;
    }

    // 실제 token 생성 로직
    private String createToken(AuthInfo authInfo,  Long tokenExpiration) {
        Claims claims = Jwts.claims();
        claims.put("id", authInfo.getId());
        claims.put("roleType", authInfo.getRoleType().name());
        Date tokenExpiresIn = new Date(new Date().getTime() + tokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(tokenExpiresIn)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
    }

}
