package com.softeer.backend.bo_domain.admin.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.softeer.backend.bo_domain.admin.dto.event.DrawEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.dto.event.FcfsEventTimeRequestDto;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventPageService {

    private FcfsSettingRepository fcfsSettingRepository;

    private DrawSettingRepository drawSettingRepository;

    public void updateFcfsEventTime(FcfsEventTimeRequestDto fcfsEventTimeRequestDto) {

        List<FcfsSetting> fcfsSettingList = fcfsSettingRepository.findAll(Sort.by(Sort.Order.asc("id")));

        LocalDate startDate = fcfsEventTimeRequestDto.getStartDate();
        LocalDate endDate = fcfsEventTimeRequestDto.getEndDate();
        LocalTime startTime = fcfsEventTimeRequestDto.getStartTime();

        updateFcfsSetting(fcfsSettingList.get(0), startDate, startTime);
        updateFcfsSetting(fcfsSettingList.get(1), endDate, startTime);

        LocalDate nextWeekStartDate = startDate.plusWeeks(1);
        LocalDate nextWeekEndDate = endDate.plusWeeks(1);

        updateFcfsSetting(fcfsSettingList.get(2), nextWeekStartDate, startTime);
        updateFcfsSetting(fcfsSettingList.get(3), nextWeekEndDate, startTime);

        DrawSetting drawSetting = drawSettingRepository.findAll().get(0);
        updateDrawSetting(drawSetting, startDate, endDate);

    }

    private void updateFcfsSetting(FcfsSetting setting, LocalDate date, LocalTime time) {

        LocalDateTime newStartTime = LocalDateTime.of(date, time);
        LocalDateTime newEndTime = newStartTime.plusHours(2);

        setting.setStartTime(newStartTime);
        setting.setEndTime(newEndTime);
    }

    private void updateDrawSetting(DrawSetting drawSetting, LocalDate startDate, LocalDate endDate) {
        LocalDate startDateOfDraw = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        LocalDate endDateOfPreviousWeek = endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        LocalDate endDateOfDraw = endDateOfPreviousWeek.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

        drawSetting.setStartDate(startDateOfDraw);
        drawSetting.setEndDate(endDateOfDraw);
    }

    public void updateDrawEventTime(DrawEventTimeRequestDto drawEventTimeRequestDto) {
        DrawSetting drawSetting = drawSettingRepository.findAll().get(0);

        drawSetting.setStartTime(drawEventTimeRequestDto.getStartTime());
        drawSetting.setEndTime(drawEventTimeRequestDto.getEndTime());
    }
}
