package org.project.pack.services;

import org.project.pack.entity.Room;
import org.project.pack.entity.Schedule;
import org.project.pack.repository.RoomRepository;
import org.project.pack.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public void saveSchedule(Long roomId, String date, String startTime, String endTime, String content) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));

        LocalDate scheduleDate;
        LocalTime scheduleTime;
        LocalTime endscheduleTime;
        // 날짜 파싱
        try {
            scheduleDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format is yyyy-MM-dd.");
        }

        // 시간 파싱
        try {
            scheduleTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Expected format is HH:mm.");
        }
        // 종료 시간 파싱
        try {
            endscheduleTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid end time format. Expected format is HH:mm:ss.");
        }


        Schedule schedule = new Schedule();
        schedule.setRoom(room);
        schedule.setScheduleDate(scheduleDate);
        schedule.setScheduleTime(scheduleTime);
        schedule.setEndScheduleTime(endscheduleTime);
        schedule.setContent(content);

        scheduleRepository.save(schedule);
    }
}
