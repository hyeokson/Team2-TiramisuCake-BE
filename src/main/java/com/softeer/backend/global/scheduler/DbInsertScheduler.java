package com.softeer.backend.global.scheduler;

import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.fo_domain.user.domain.User;
import com.softeer.backend.fo_domain.user.exception.UserException;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.util.EventLockRedisUtil;
import com.softeer.backend.global.util.FcfsRedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbInsertScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final FcfsRedisUtil fcfsRedisUtil;
    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;
    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;
    private final FcfsRepository fcfsRepository;


    private ScheduledFuture<?> scheduledFuture;

    @PostConstruct
    public void init() {
        scheduleTask();

    }

    public void scheduleTask() {
        scheduledFuture = taskScheduler.schedule(this::updateFcfsSetting, new CronTrigger("0 0 1 * * *"));
    }

    @Transactional
    protected void updateFcfsSetting() {
        LocalDate now = LocalDate.now();
        if (now.isBefore(drawSettingManager.getStartDate().plusDays(1)))
            return;

        if(now.isAfter(drawSettingManager.getEndDate().plusDays(1)))
            stopScheduler();

        int totalVisitorsCount = eventLockRedisUtil.getData(RedisKeyPrefix.TOTAL_VISITORS_COUNT_PREFIX.getPrefix());
        eventLockRedisUtil.deleteData(RedisKeyPrefix.TOTAL_VISITORS_COUNT_PREFIX.getPrefix());

        int fcfsParticipantCount = 0;
        int drawParticipantCount = 0;

        if(fcfsSettingManager.getRoundForScheduler(now)!=-1){
            fcfsSettingManager.setFcfsClosed(false);

            int round = fcfsSettingManager.getRoundForScheduler(now);

            Map<String, Integer> participantIds = fcfsRedisUtil.getHashEntries(RedisKeyPrefix.FCFS_CODE_USERID_PREFIX.getPrefix() + round);
            participantIds.forEach((code, userId) -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.error("user not found in saveFcfsWinners method.");
                            return new UserException(ErrorStatus._NOT_FOUND);
                        });

                Fcfs fcfs = Fcfs.builder()
                        .user(user)
                        .round(round)
                        .code(code)
                        .build();
                fcfsRepository.save(fcfs);
            });

            fcfsParticipantCount += fcfsRedisUtil.getValue(RedisKeyPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);

            fcfsRedisUtil.clearValue(RedisKeyPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);
            fcfsRedisUtil.clearIntegerSet(RedisKeyPrefix.FCFS_LOCK_PREFIX.getPrefix() + round);
            fcfsRedisUtil.clearStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round);
            fcfsRedisUtil.clearHash(RedisKeyPrefix.FCFS_CODE_USERID_PREFIX.getPrefix() + round);
        }

        // TODO: drawParticipantCount에 추첨 이벤트 참가자 수 할당하기

        eventParticipationRepository.save(EventParticipation.builder()
                .visitorCount(totalVisitorsCount)
                .fcfsParticipantCount(fcfsParticipantCount)
                .drawParticipantCount(drawParticipantCount)
                .eventDate(now.minusDays(1))
                .build());

    }

    /**
     * Schedular의 작업을 비활성화 시키는 메서드
     */
    public void stopScheduler() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }
}
