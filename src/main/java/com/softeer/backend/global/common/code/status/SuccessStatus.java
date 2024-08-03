package com.softeer.backend.global.common.code.status;

import com.softeer.backend.global.common.code.BaseCode;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 성공 응답 코드를 관리하는 Enum 클래스
 */
@Getter
@RequiredArgsConstructor
public enum SuccessStatus implements BaseCode {
    // Success
    _OK(HttpStatus.OK, "SUCCESS_200", "OK"),

    // 선착순
    _FCFS_SUCCESS(HttpStatus.OK, "FCFS_SUCCESS", "선착순 당첨 성공 응답"),
    _FCFS_FAIL(HttpStatus.OK, "FCFS_FAIL", "선착순 당첨 실패 응답");

    // 예외의 Http 상태값
    private final HttpStatus httpStatus;

    // 예외의 커스텀 코드값
    private final String code;

    // 예외 메시지
    private final String message;


    /**
     * 성공 응답 정보를 갖고있는 ReasonDto를 반환하는 메서드
     *
     * @return ReasonDto 객체
     */
    @Override
    public ResponseDto.ReasonDto getReason() {
        return ResponseDto.ReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(true)
                .code(this.code)
                .message(this.message)
                .build();
    }

    /**
     * 성공 코드를 반환하는 메서드
     *
     * @return 커스텀 코드값
     */
    @Override
    public String getCode(){
        return this.code;
    }

    /**
     * 성공 메시지를 반환하는 메서드
     *
     * @return 예외 메시지
     */
    @Override
    public String getMsg(){
        return this.message;
    }
}
