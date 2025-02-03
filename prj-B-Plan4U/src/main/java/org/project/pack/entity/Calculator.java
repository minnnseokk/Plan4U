package org.project.pack.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CalculatorTable")
@SequenceGenerator(
	allocationSize = 1,
	initialValue = 0,
	name = "CalculatorSeq",
	sequenceName = "CaculatorSeq"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Calculator {
	@Id
	@GeneratedValue(generator = "CalculatorSeq", strategy = GenerationType.SEQUENCE)
	Long id; // 어쩔수 없이 만드는 id 값 무시해도 됨
	
	Long numPeople; // 인원 수
	
	Long sumPrice; // 금액 총합 
	
	Long resultPrice; // 금액 연산 결과
	
    @ManyToOne
    @JoinColumn(name = "roomId", referencedColumnName = "id")
    Room room; // 참조키
    
    @Column(name = "submissionDate")
    private LocalDate submissionDate; // 데이터 전송 날짜
}
