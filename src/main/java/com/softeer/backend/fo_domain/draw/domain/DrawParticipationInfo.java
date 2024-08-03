package com.softeer.backend.fo_domain.draw.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "draw_participation_info")
public class DrawParticipationInfo {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "draw_winning_count")
    private Integer drawWinningCount;

    @Column(name = "draw_losing_count")
    private Integer drawLosingCount;

    @Column(name = "draw_participation_count")
    private Integer drawParticipationCount;

    @Builder
    public DrawParticipationInfo(Integer userId, Integer drawWinningCount, Integer drawLosingCount, Integer drawParticipationCount) {
        this.userId = userId;
        this.drawWinningCount = drawWinningCount;
        this.drawLosingCount = drawLosingCount;
        this.drawParticipationCount = drawParticipationCount;
    }
}
