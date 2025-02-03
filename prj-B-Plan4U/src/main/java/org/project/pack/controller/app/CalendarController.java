package org.project.pack.controller.app;

import java.time.LocalDate;
import java.util.List;

import org.project.pack.annotations.AuthUser;
import org.project.pack.entity.Room;
import org.project.pack.entity.Schedule;
import org.project.pack.entity.User;
import org.project.pack.repository.RoomRepository;
import org.project.pack.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app")
public class CalendarController {
	
	@Autowired
	RoomRepository roomRep;
	
	@Autowired
	ScheduleRepository scheduleRep;
	
    @GetMapping("/calendar/{roomId}")
    public String showCalendar(@PathVariable("roomId") Long roomId, Model model,@AuthUser User user,
            HttpSession session) {
        model.addAttribute("userName",user.getName());
        session.setAttribute("roomId", roomId);
    	Room room = roomRep.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + roomId));
		model.addAttribute("room", room);// 로그 리스트 비우기
		
        return "calendar";
    }

    @GetMapping("/schedule/{roomId}")
	public String showMultiSchedule(@PathVariable("roomId") Long roomId,
	            @RequestParam("dates") List<String> dates,
	            Model model,@AuthUser User user,
		          HttpSession session) {
    model.addAttribute("userName",user.getName());
    session.setAttribute("roomId", roomId);
	Room room = roomRep.findById(roomId)
	.orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + roomId));
	
	model.addAttribute("room", room);
	model.addAttribute("dates", dates);
	return "schedule";
	}
    
    
}

