package com.softeer.backend.fo_domain.draw.dto.result;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawHistoryResponseDto {
    private boolean isDrawWin;
}
