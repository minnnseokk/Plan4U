package org.project.pack.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.project.pack.entity.Calculator;
import org.project.pack.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>{
	 List<Schedule> findByScheduleDate(LocalDate scheduleDate);
	 List<Schedule> findByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
	 List<Schedule> findAllByRoom_IdAndScheduleDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);
	 public void deleteAllByroom_id(Long id);
	 
	 @Transactional
	 public void deleteByRoom_IdAndContentAndScheduleTimeAndEndScheduleTimeAndScheduleDate(Long roomId, String content, LocalTime startTime, LocalTime endTime, LocalDate date);
}
