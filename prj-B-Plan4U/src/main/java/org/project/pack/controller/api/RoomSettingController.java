package org.project.pack.controller.api;

import org.project.pack.annotations.AuthUser;
import org.project.pack.entity.Room;
import org.project.pack.entity.User;
import org.project.pack.repository.CalculatorRepository;
import org.project.pack.repository.GalleryRepository;
import org.project.pack.repository.GuestsRepository;
import org.project.pack.repository.MemoRepository;
import org.project.pack.repository.RoomRepository;
import org.project.pack.repository.ScheduleRepository;
import org.project.pack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/api")
public class RoomSettingController {
	@Autowired
	RoomRepository roomRep;
	
	@Autowired
	UserRepository userRep;
	
	@Autowired
	GuestsRepository guestsRep;
	
	@Autowired
	MemoRepository memoRep;
	
	@Autowired
	GalleryRepository galleryRep;
	
	@Autowired
	CalculatorRepository calculatorRep;
	
	@Autowired
	ScheduleRepository scheduleRep;
	
	@PostMapping("/delroom/{id}")
	@Transactional
	public ResponseEntity<Void> delroom(@PathVariable Long id) {
	    calculatorRep.deleteAllByroom_id(id);
	    galleryRep.deleteAllByroom_id(id);
	    scheduleRep.deleteAllByroom_id(id);
	    memoRep.deleteAllByroom_id(id);
	    guestsRep.deleteAllByroom_id(id);
	    roomRep.deleteById(id);
	    
	    return ResponseEntity.noContent().build();
	}

	
	@PostMapping("/delguest/{id}")
	@Transactional
	public String delguest(@PathVariable Long id, @RequestParam String email) {
		User user = userRep.findByEmail(email);
		Room room = roomRep.findById(id).orElse(null);
		guestsRep.deleteByUserAndRoom(user, room);
		
		return "redirect:/app/roomsetting/{id}";
	}    
	@PostMapping("/delme/{id}")
	@Transactional
	public String delme(@PathVariable Long id, @AuthUser User user, RedirectAttributes redirectAttributes) {
		Room room = roomRep.findById(id).orElse(null);
		guestsRep.deleteByUserAndRoom(user, room);
		redirectAttributes.addFlashAttribute("alertMessage", "방에서 퇴장하셨습니다");
		 return "redirect:/app/mainpage";
	}
}















