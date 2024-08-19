package com.softeer.backend.fo_domain.mainpage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class MainPageEventInfoResponseDto {

    private String startDate;

    private String endDate;

    private String fcfsInfo;

    private String totalDrawWinner;

    private String remainDrawCount;

    private String fcfsHint;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fcfsStartTime;
}
