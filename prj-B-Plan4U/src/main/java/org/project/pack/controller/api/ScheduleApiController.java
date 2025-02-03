package org.project.pack.controller.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.project.pack.entity.Schedule;
import org.project.pack.repository.ScheduleRepository;
import org.project.pack.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRepository scheduleRep;
    
    Schedule schedule = new Schedule();
    
    @PostMapping("/save")
    public ResponseEntity<String> saveSchedule(
            @RequestParam("dates[]") List<String> dates,
            @RequestParam("startTimes[]") List<String> times,
            @RequestParam("endTimes[]") List<String> endTimes,
            @RequestParam("contents[]") List<String> contents,
            @RequestParam("roomId") Long roomId) {
        
        try {
            if (dates.size() != times.size() || dates.size() != contents.size()) {
                throw new IllegalArgumentException("Dates, times, and contents must have the same size.");
            }
            
            for (int i = 0; i < dates.size(); i++) {
                scheduleService.saveSchedule(roomId, dates.get(i), times.get(i), endTimes.get(i), contents.get(i));
            }
            
            return ResponseEntity.ok("Schedules saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving schedules: " + e.getMessage());
        }
    }
    @PostMapping("/delete/{roomId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long roomId, 
    		@RequestParam("content") String content,
    		@RequestParam("startTime") String startTime,
    		@RequestParam("endTime") String endTime,
    		@RequestParam("date") String date) {
    	
    	schedule.setFormattedTime(startTime);
    	LocalTime start = schedule.getScheduleTime();
    	schedule.setFormattedEndScheduleTime(endTime);
    	LocalTime end = schedule.getEndScheduleTime();
    	schedule.setFormattedDate(date);
    	LocalDate scheduleDate = schedule.getScheduleDate();
    	
    	scheduleRep.deleteByRoom_IdAndContentAndScheduleTimeAndEndScheduleTimeAndScheduleDate(roomId,content,start,end,scheduleDate);
        try {
            return ResponseEntity.ok("Schedule deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting schedule: " + e.getMessage());
        }
    }
    @PostMapping("/fetch/{roomId}")
    public  List<Schedule> getSchedules(@PathVariable Long roomId,
                                       @RequestParam("year") int year,
                                       @RequestParam("month") int month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        
        List<Schedule> schedules = new ArrayList<Schedule>();
        schedules = scheduleRep.findAllByRoom_IdAndScheduleDateBetween(roomId, firstDayOfMonth, lastDayOfMonth);
        
        return schedules;
    }
}