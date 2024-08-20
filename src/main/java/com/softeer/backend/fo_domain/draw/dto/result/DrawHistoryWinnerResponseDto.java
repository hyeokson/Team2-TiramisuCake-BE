package com.softeer.backend.fo_domain.draw.dto.result;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 추첨 이벤트 당첨 내역이 있는 경우 응답 DTO 클래스
 */
@Data
@SuperBuilder
public class DrawHistoryWinnerResponseDto extends DrawHistoryResponseDto {
    private WinModal winModal;
}
