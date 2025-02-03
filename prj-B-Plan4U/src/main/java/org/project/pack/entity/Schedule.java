package org.project.pack.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ScheduleTable")
@SequenceGenerator(
    name = "ScheduleSeq",
    sequenceName = "ScheduleSeq",
	allocationSize = 1,
	initialValue = 0
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Schedule {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ScheduleSeq")
    Long id;
	
	LocalDate scheduleDate;
	LocalTime scheduleTime; // 기본으로 존재한 시작 시간
	LocalTime endScheduleTime; // 새로 추가된 종료 시간
	String content;
	
	@ManyToOne
    @JoinColumn(name = "invitedroom", referencedColumnName = "id")
	@JsonBackReference // json 스크립트로 반환하려면 룸 값이 중복되어 순환참조 되는 것을 막아줘야함
    Room room;
	
	@Transient
    public String getFormattedDate() {
        return scheduleDate != null ? scheduleDate.format(DateTimeFormatter.ofPattern("yy/MM/dd")) : null;
    }

    // 문자열 시간 값을 LocalTime으로 변환하는 메서드
	@Transient
    public void setFormattedDate(String formattedDate) {
        if (formattedDate != null && !formattedDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
            this.scheduleDate = LocalDate.parse(formattedDate, formatter);
        }
    }
	

    // 문자열 시간 값을 LocalTime으로 변환하는 메서드
    @Transient
    public void setFormattedTime(String formattedTime) {
        if (formattedTime != null && !formattedTime.isEmpty()) {
            this.scheduleTime = LocalTime.parse(formattedTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }
    
    @Transient
    public String getFormattedEndScheduleTime() {
        return endScheduleTime != null ? endScheduleTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) : null;
    }

    @Transient
    public void setFormattedEndScheduleTime(String formattedEndScheduleTime) {
        if (formattedEndScheduleTime != null && !formattedEndScheduleTime.isEmpty()) {
            this.endScheduleTime = LocalTime.parse(formattedEndScheduleTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }
    @Override
    public String toString() {
        return "Schedule{" +
               "id=" + id +
               ", scheduleDate=" + scheduleDate +
               ", scheduleTime=" + scheduleTime +
               ", endScheduleTime=" + endScheduleTime +
               ", content='" + content + '\'' +
               ", roomId=" + (room != null ? room.getId() : "null") + // room 객체가 null일 수 있음
               '}';
    }
}























