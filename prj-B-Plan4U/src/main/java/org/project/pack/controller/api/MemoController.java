package org.project.pack.controller.api;

import java.time.LocalDateTime;
import java.util.Date;

import org.project.pack.annotations.AuthUser;
import org.project.pack.entity.Memo;
import org.project.pack.entity.Room;
import org.project.pack.entity.User;
import org.project.pack.repository.MemoRepository;
import org.project.pack.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class MemoController {
	
	@Autowired
	RoomRepository roomRep;

	@Autowired
	MemoRepository memoRep;
	
	@PostMapping("/memo/{id}")
	public String MemoDB(
			@RequestParam(name = "memo") String context,
			@PathVariable Long id,
			@AuthUser User user,
			Model model
			) {
		Room roomid = roomRep.findById(id).orElse(null);
		LocalDateTime now = LocalDateTime.now();
		
		Memo memo = new Memo();
		memo.setWritedate(now);
		memo.setRoom(roomid);
		memo.setMemo(context);
		memo.setUser(user);
		memoRep.save(memo);
		
		model.addAttribute("id", id);
		
		return "redirect:/app/memo/{id}";
	}
}

























