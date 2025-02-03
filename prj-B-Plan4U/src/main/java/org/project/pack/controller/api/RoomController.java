package org.project.pack.controller.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.project.pack.annotations.AuthUser;
import org.project.pack.classes.UD;
import org.project.pack.entity.Guests;
import org.project.pack.entity.Room;
import org.project.pack.entity.User;
import org.project.pack.repository.CalculatorRepository;
import org.project.pack.repository.GalleryRepository;
import org.project.pack.repository.GuestsRepository;
import org.project.pack.repository.MemoRepository;
import org.project.pack.repository.RoomRepository;
import org.project.pack.repository.ScheduleRepository;
import org.project.pack.repository.UserRepository;
import org.project.pack.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;

@Controller
@RequestMapping("/api")
public class RoomController {
	
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
	
	@Autowired
	RoomService roomService;
	
	public List<String> invitedList = new ArrayList<>(); // 게스트
	
	@PostMapping("/createroom")
	public String createRoom(
	        @RequestParam(name = "title") String title, 
	        @RequestParam(name = "subtitle", defaultValue = " ") String subtitle, 
	        @RequestPart(name = "files", required = false) MultipartFile files, 
	        @AuthUser User user,
	        HttpServletRequest req,
	        Model model) {
	    
	    // 사용자 확인
	    if (user == null) {
	        model.addAttribute("error", "사용자를 찾을 수 없습니다.");
	        return "login";
	    }

	    // 방 생성 및 저장
	    Room room = new Room();
	    room.setTitle(title);
	    room.setSubtitle(subtitle);
	    room.setHost(user);
	    room.setLastUpdated(new Date());

	    roomRep.save(room);

	    // 이미지 업로드 및 처리
	    if (files != null && !files.isEmpty()) {
	        String result = roomService.uploadRoomImage(files, room.getId(), req);
	        if ("{\"message\":\"error\"}".equals(result)) {
	            model.addAttribute("error", "이미지 업로드 중 오류가 발생했습니다.");
	            return "errorPage";
	        }
	    } else {
	        // 이미지 파일이 제공되지 않았을 때 기본 이미지 설정
	        room.setImagePath("/image/noimage.png");
	        roomRep.save(room);
	    }

	    // 게스트 초대 처리
	    List<String> guestEmails = invitedList == null ? new ArrayList<>() : invitedList;
	    for (String email : guestEmails) {
	        email = email.trim(); // 이메일 공백 제거
	        User guest = userRep.findByEmail(email);
	        if (guest != null) {
	            Guests guestRecord = new Guests();
	            guestRecord.setRoom(room);
	            guestRecord.setUser(guest);
	            guestsRep.save(guestRecord);
	        }
	    }

	    // 생성 후 초대 리스트 초기화
	    invitedList.clear();
	    
	    return "redirect:/app/mainpage";
	}

	
	@PostMapping("/invite")
	public String manageInvitedList(@RequestParam String email, Model model){ // 이메일을 받아와서
		
		
		if(!invitedList.contains(email)) { // 리스트에 이메일이 없으면 추가하기
			invitedList.add(email);
		} else {
			invitedList.remove(email);
		}
		model.addAttribute("finalDatas",invitedList);
		return "createRoom :: finalList";
	}
	
	
	
	@PostMapping("/search")
	public String searchUserByName(@RequestParam String name, Model model, @AuthUser User userone){ // 현재 로그인중인 계정의 객체정보를 불러운다
		
		List <User> allusers = userRep.findAllByNameLike("%"+name+"%"); // 유저목록 전부 찾아서 저장하는 메서드
//		 유저에서 현재 사용자를 제거하고 찾게 하도록 변경
	    List<User> users = allusers.stream()
                .filter(user -> !user.getId().equals(userone.getId()))
                .collect(Collectors.toList());		

	    
		List<Map<String , String>> nameAndEmails = new ArrayList<>(); // 해시맵을 리스트로 저장한 방식

		for(User user : users) {
			if(user.getName().contains(name)) { // 글자 하나하나마다 이름으로 인식
				Map<String,String> userdatas = new HashMap<>(); 
				userdatas.put("name", user.getName());
				userdatas.put("email", user.getEmail()); // 유저 데이터 저장
				nameAndEmails.add(userdatas); // 맵 방식으로 이름, 이메일 저장
			}
		}
		model.addAttribute("searchResult", nameAndEmails);
		
		return "createRoom :: emailList"; // emailList th:Fragment
	}
	   @PostMapping("/addguest/{id}")
	   public String addguest(
	         @PathVariable Long id,
	         Model model
	         ){
	      Room room = roomRep.findById(id).orElse(null);
	      List<Guests> tmpGuests = guestsRep.findByRoom_id(id); // 룸에 존재하는 게스트 리스트
	      List<User> existedGuests = new ArrayList<User>();
	      for(Guests user : tmpGuests) {
	         existedGuests.add(user.getUser());
	      }
	      List<String> guestEmails = invitedList == null ? new ArrayList<>() : invitedList;
	      
	      List<String> duplicates = new ArrayList<>(); // 중복 이메일 저장 리스트
	      
	       for (String email : guestEmails) {
	           email = email.trim(); // 이메일 공백 제거
	           User guest = userRep.findByEmail(email);
	           if(!existedGuests.contains(guest)) { // 중복으로 초대된 게스트인지?
	              if(guest != null) {
	                  Guests guestRecord = new Guests();
	                  guestRecord.setRoom(room);
	                  guestRecord.setUser(guest);
	                  guestsRep.save(guestRecord);
	              }
	           }else {
	               duplicates.add(email); // 중복 이메일 추가
	           }
	       }
	       // 생성 후 초대 리스트 초기화
	       invitedList.clear();
	       
	       if (!duplicates.isEmpty()) {
	           model.addAttribute("alertMessage", "이미 존재하는 유저가 있습니다: " + String.join(", ", duplicates));
	       }
	      return "redirect:/app/roomsetting/{id}";
	   }
}




























