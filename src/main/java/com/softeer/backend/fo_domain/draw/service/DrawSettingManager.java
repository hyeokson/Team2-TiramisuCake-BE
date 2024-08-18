package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.Draw;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.user.domain.User;
import com.softeer.backend.fo_domain.user.exception.UserException;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.util.DrawRedisUtil;
import com.softeer.backend.global.util.EventLockRedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Component
@RequiredArgsConstructor
public class DrawSettingManager {
    private final DrawRepository drawRepository;
    private final DrawSettingRepository drawSettingRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final DrawRedisUtil drawRedisUtil;
    private final UserRepository userRepository;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int winnerNum1;
    private int winnerNum2;
    private int winnerNum3;

    // @PostConstruct로 생성됐을 시 세팅정보 가져오기
    // 스케줄러로 01:00:00에 redis 임시 목록 삭제하기

    @PostConstruct
    public void initializeDrawSettingManager() {
        DrawSetting drawSetting = drawSettingRepository.findById(1)
                .orElseThrow(() -> new DrawException(ErrorStatus._NOT_FOUND));

        startDate = drawSetting.getStartDate();
        endDate = drawSetting.getEndDate();
        startTime = drawSetting.getStartTime();
        endTime = drawSetting.getEndTime();
        winnerNum1 = drawSetting.getWinnerNum1();
        winnerNum2 = drawSetting.getWinnerNum2();
        winnerNum3 = drawSetting.getWinnerNum3();

        // 매일 01:00:00에 redis 당첨자 목록 데이터베이스에 저장
        taskScheduler.schedule(this::addWinnerToDatabase, new CronTrigger("0 0 1 * * *"));

        // 매일 01:00:00에 redis 당첨자 목록 삭제하기
        taskScheduler.schedule(this::deleteWinnerSetFromRedis, new CronTrigger("0 0 1 * * *"));
    }

    /**
     * 당첨자 목록 모두 삭제
     */
    private void deleteWinnerSetFromRedis() {
        String drawWinnerKey;
        for (int ranking = 1; ranking < 4; ranking++) {
            drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;
            drawRedisUtil.deleteAllSetData(drawWinnerKey);
        }
    }

    /**
     * 당첨자 목록 모두 데이터베이스에 저장
     */
    private void addWinnerToDatabase() {
        String drawWinnerKey;
        for (int ranking = 1; ranking < 4; ranking++) {
            drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;
            Set<Integer> winnerSet = drawRedisUtil.getAllDataAsSet(drawWinnerKey);

            LocalDateTime winningDate = LocalDateTime.now().minusHours(2); // 하루 전 날 오후 11시로 설정

            for (Integer userId : winnerSet) {
                User user = userRepository.findById(userId).orElseThrow(
                        () -> new UserException(ErrorStatus._NOT_FOUND));

                Draw draw = Draw.builder()
                        .user(user)
                        .rank(ranking)
                        .winningDate(winningDate)
                        .build();

                drawRepository.save(draw);
            }
        }
    }

    public void setDrawDate(DrawSetting drawSetting) {
        this.startDate = drawSetting.getStartDate();
        this.endDate = drawSetting.getEndDate();
    }

    public void setDrawTime(DrawSetting drawSetting) {
        this.startTime = drawSetting.getStartTime();
        this.endTime = drawSetting.getEndTime();
    }

    public void setDrawWinnerNum(int winnerNum1, int winnerNum2, int winnerNum3) {
        this.winnerNum1 = winnerNum1;
        this.winnerNum2 = winnerNum2;
        this.winnerNum3 = winnerNum3;
    }
}
