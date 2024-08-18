package com.softeer.backend.fo_domain.draw.util;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import com.softeer.backend.global.staticresources.util.StaticResourcesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DrawModalGenerateUtil {
    private final StaticResourcesUtil staticResourcesUtil;

    /**
     * @return 등수에 따른 WinModal을 반환
     */
    public WinModal generateWinModal(int ranking) {
        if (ranking == 1) {
            return generateFirstWinModal();
        } else if (ranking == 2) {
            return generateSecondWinModal();
        } else if (ranking == 3) {
            return generateThirdWinModal();
        } else {
            return generateFullAttendModal();
        }
    }

    /**
     * @return 1등 WinModal 반환
     */
    private WinModal generateFirstWinModal() {
        return WinModal.builder()
                .title(staticResourcesUtil.getData("DRAW_WINNER_MODAL_TITLE"))
                .subtitle(staticResourcesUtil.getData("DRAW_FIRST_WINNER_MODAL_SUBTITLE"))
                .img(staticResourcesUtil.getData("draw_reward_image_1"))
                .description(staticResourcesUtil.getData("DRAW_WINNER_MODAL_DESCRIPTION"))
                .build();
    }

    /**
     * @return 2등 WinModal 반환
     */
    private WinModal generateSecondWinModal() {
        return WinModal.builder()
                .title(staticResourcesUtil.getData("DRAW_WINNER_MODAL_TITLE"))
                .subtitle(staticResourcesUtil.getData("DRAW_SECOND_WINNER_MODAL_SUBTITLE"))
                .img(staticResourcesUtil.getData("draw_reward_image_2"))
                .description(staticResourcesUtil.getData("DRAW_WINNER_MODAL_DESCRIPTION"))
                .build();
    }

    /**
     * @return 3등 WinModal 반환
     */
    private WinModal generateThirdWinModal() {
        return WinModal.builder()
                .title(staticResourcesUtil.getData("DRAW_WINNER_MODAL_TITLE"))
                .subtitle(staticResourcesUtil.getData("DRAW_THIRD_WINNER_MODAL_SUBTITLE"))
                .img(staticResourcesUtil.getData("draw_reward_image_3"))
                .description(staticResourcesUtil.getData("DRAW_WINNER_MODAL_DESCRIPTION"))
                .build();
    }

    /**
     * 7일 연속 출석자 상품 정보 반환 메서드
     *
     * @return FullAttendModal 반환
     */
    public WinModal generateFullAttendModal() {
        return WinModal.builder()
                .title(staticResourcesUtil.getData("FULL_ATTEND_MODAL_TITLE"))
                .subtitle(staticResourcesUtil.getData("FULL_ATTEND_MODAL_SUBTITLE"))
                .img(staticResourcesUtil.getData("attendance_reward_image"))
                .description(staticResourcesUtil.getData("FULL_ATTEND_MODAL_DESCRIPTION"))
                .build();
    }
}
