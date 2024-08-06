package com.softeer.backend.global.common.constant;

import lombok.Getter;

@Getter
public enum RedisLockPrefix {
    FCFS_LOCK_PREFIX("LOCK:FCFS_WINNER"),
    DRAW_LOCK_PREFIX("LOCK:DRAW_WINNER"),
    FCFS_PARTICIPANT_COUNT_PREFIX("FCFS_PARTICIPANT_COUNT_");

    private final String prefix;

    RedisLockPrefix(String prefix) {
        this.prefix = prefix;
    }
}
