package org.project.pack.controller.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.project.pack.entity.Calculator;
import org.project.pack.entity.Room;
import org.project.pack.repository.CalculatorRepository;
import org.project.pack.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
public class CalculatorController {


	@Autowired
	RoomRepository roomRep;
	
	@Autowired
	CalculatorRepository calculatorRep;
	
	
	@PostMapping("/calculator/{id}")
	public String getCalculator(
			@RequestParam Long numPeople,
            @RequestParam Long totalSum,
            @RequestParam Long resultPrice, // 계산기에서 가져온 데이터
            RedirectAttributes redirectAttributes,
			@PathVariable Long id, Model model) {
		Room room = roomRep.findById(id).orElseThrow(null);
        model.addAttribute("room", room);
        
        Calculator calculator = new Calculator();
        calculator.setRoom(room);
        calculator.setNumPeople(numPeople);
        calculator.setSumPrice(totalSum);
        calculator.setResultPrice(resultPrice);
        calculator.setSubmissionDate(LocalDate.now());
        
        calculatorRep.save(calculator);
        
        // 로그 저장
        String oneLineLog = "총합: " + totalSum + "원 (인원수 " + numPeople + " / 인당 " + resultPrice + "원)";
        
        // 리다이렉트 시 로그 데이터 전달
        redirectAttributes.addFlashAttribute("oneLineLog", oneLineLog);
        
		return "redirect:/app/calculator/{id}";
	}
}
