package org.project.pack.entity;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MemoTable")
@SequenceGenerator(
    name = "MemoSeq",
    sequenceName = "MemoSeq",
	allocationSize = 1,
	initialValue = 0
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memo {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MemoSeq")
    Long id;
	
	@Column(length = 50000)
	String memo;
	
	@Column(nullable = false) 
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime writedate; 
	
	@ManyToOne
    @JoinColumn(name = "invitedroom", referencedColumnName = "id")
    Room room;

    @ManyToOne
    @JoinColumn(name = "writer", referencedColumnName = "id")
    User user;
}






















