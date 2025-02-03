package org.project.pack.controller.app;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.project.pack.entity.Calculator;
import org.project.pack.annotations.AuthUser;
import org.project.pack.controller.api.FileIOController;
import org.project.pack.entity.Gallery;
import org.project.pack.entity.Guests;
import org.project.pack.entity.Room;
import org.project.pack.entity.User;
import org.project.pack.entity.Memo;
import org.project.pack.repository.CalculatorRepository;
import org.project.pack.repository.GalleryRepository;
import org.project.pack.repository.GuestsRepository;
import org.project.pack.repository.MemoRepository;
import org.project.pack.repository.RoomRepository;
import org.project.pack.repository.ScheduleRepository;
import org.project.pack.repository.UserRepository;
import org.project.pack.services.GalleryService;
import org.project.pack.services.RoomService;
import org.project.pack.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app")
public class AppController {

	ScheduleService scheduleService;
	@Autowired
	UserRepository userRep;
	
	@Autowired
	RoomRepository roomRep;

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
	GalleryService galleryService;
	
	@Autowired
    private RoomService roomService;

	List<String> priceLogLists = new ArrayList<>();

	@GetMapping("/login")
	public String Login() {
		return "login";
	}

	@GetMapping("/base")
	public String Base() {
		return "base";
	}

	@GetMapping("/createRoom")
	public String CreateRoom() {
		return "createRoom";
	}
	@GetMapping("/roomsetting/{id}")
	public String RoomSetting(@PathVariable Long id, Model model,@AuthUser User user, RedirectAttributes redirectAttributes) {
		boolean isHost = false;
		Room room = roomRep.findById(id).orElse(null);
		// 기본값은 false 로 설정
		if(room.getHost().getId()==user.getId()) {
			isHost = true;
			model.addAttribute("isHost", isHost);
		}
		List<User> users = roomService.getUsersByRoomId(id);
		model.addAttribute("users", users);
		model.addAttribute("room", room);
		return "roomsetting";
	}

	@GetMapping("/mainpage")
	public String mainpage(@RequestParam(name = "searchData", required = false) String searchData,
			@RequestParam(name = "searchCategory", required = false, defaultValue = "title") String searchCategory,
			Model model, @AuthUser User user) {

		// 사용자가 호스트인 방 조회
		List<Room> hostRooms = roomRep.findByHost(user);

		// 사용자가 게스트로 있는 방 ID 목록 조회
		List<Guests> guestRecords = guestsRep.findByUser(user);
		List<Long> guestRoomIds = new ArrayList<>();
		for (Guests guest : guestRecords) {
			if (guest.getRoom() != null) {
				guestRoomIds.add(guest.getRoom().getId());
			}
		}

		// 방 ID로 방 조회
		List<Room> guestRooms = roomRep.findByIdIn(guestRoomIds);

		// 두 리스트를 합칩니다.
		List<Room> combinedRooms = new ArrayList<>();
		combinedRooms.addAll(hostRooms);
		combinedRooms.addAll(guestRooms);

		// 중복된 방 제거
		Set<Room> uniqueRooms = new HashSet<>(combinedRooms);

		// 검색 조건에 따라 필터링
		if (searchData != null && !searchData.isEmpty()) { // 입력한 값이 존재하면
			if ("hostName".equals(searchCategory)) { // 호스트 이름모드
				uniqueRooms = uniqueRooms.stream().filter(room -> room.getHost().getName().contains(searchData)) // 호스트의
																													// 이름에
																													// 서치데이터를
																													// 포함하는지
						.collect(Collectors.toSet());
			} else { // 방제목모드
				uniqueRooms = uniqueRooms.stream().filter(room -> room.getTitle().contains(searchData)) // 방 title 에
																										// 서치데이터가 포함되는지
						.collect(Collectors.toSet()); // 필터링된 값들을 set 으로 수집
			}
		}

		// 최신 업데이트 날짜로 방 정렬
		List<Room> sortedRooms = uniqueRooms
				.stream().sorted(Comparator
						.comparing(Room::getLastUpdated, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
				.collect(Collectors.toList());

		Map <Long,List<String>> NamesByRooms =  new HashMap<Long,List<String>>();
		Map <Long,String> NameForHover =  new HashMap<Long,String>();
		
		
		for(Room room : sortedRooms) {
			List<Guests> tmpGuests = guestsRep.findByRoom(room); // 게스트 목록 
			List<String> existedGuests = new ArrayList<String>(); // 게스트 이메일 목록
			List<String> existedGuestsInfo = new ArrayList<String>(); // 게스트 이메일 목록
			for(Guests guest : tmpGuests) {
				existedGuests.add(guest.getUser().getName()); // 추출후 넣기
				existedGuestsInfo.add(guest.getUser().getName()+ '-' + guest.getUser().getEmail());
		    }
			NamesByRooms.put(room.getId(), existedGuests);
			// 리스트를 문자열로 변환 (게스트 이메일을 ' :: '로 구분)
			String guestListString = String.join("\n", existedGuestsInfo);
			NameForHover.put(room.getId(), guestListString);
		}
		
		
		// 모델에 데이터 추가
		model.addAttribute("guestListForHover", NameForHover);
		model.addAttribute("rooms", sortedRooms);
		model.addAttribute("guestList",NamesByRooms);
		

		return "mainpage";
	}

	@GetMapping("/base/{id}")
	public String getRoom(@PathVariable Long id, Model model, @AuthUser User user, HttpSession session) {
		model.addAttribute("userName", user.getName());
		session.setAttribute("roomId", id);
		Room room = roomRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + id));

		List<Memo> memoList = memoRep.findByRoomId(id);
		memoList.sort(Comparator.comparing(Memo::getWritedate).reversed());

		List<Gallery> galleries = galleryRep.findByRoomId(id);
		List<String> imageUrls = galleries.stream().flatMap(gallery -> gallery.getImagePaths().stream())
				.collect(Collectors.toList());

		List<Calculator> calculator = calculatorRep.findByRoomId(id);

		model.addAttribute("room", room);
		model.addAttribute("calculator", calculator);
		model.addAttribute("memos", memoList);
		model.addAttribute("imageUrls", imageUrls);
		priceLogLists.clear(); // 전역 로그 리스트 초기화
		return "base";
	}

	@GetMapping("/memo/{id}")
	public String Memo(Model model, @PathVariable Long id,
			@RequestParam(name = "searchMemo", required = false) String searchMemo,@AuthUser User user,
	          HttpSession session) {
	      model.addAttribute("userName",user.getName());
	      session.setAttribute("roomId", id);
		List<Memo> memoList = memoRep.findByRoomId(id);

		if (searchMemo != null && !searchMemo.isEmpty()) {
			memoList = memoList.stream().filter(memoData -> memoData.getMemo().contains(searchMemo))
					.collect(Collectors.toList());
		}
		// 최신 순으로 정렬
		memoList.sort(Comparator.comparing(Memo::getWritedate).reversed());

		model.addAttribute("memos", memoList);
		Room room = roomRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + id));
		model.addAttribute("room", room);
		priceLogLists.clear(); // 전역 로그 리스트 초기화
		return "memo";
	}

	@GetMapping("/vote/{id}")
	public String Vote(@PathVariable Long id, Model model,@AuthUser User user,
	          HttpSession session) {
	      model.addAttribute("userName",user.getName());
	      session.setAttribute("roomId", id);
		Room room = roomRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + id));
		model.addAttribute("room", room);// 로그 리스트 비우기
		priceLogLists.clear(); // 전역 로그 리스트 초기화
		return "vote";
	}

	@GetMapping("/gallery/{id}")
	public String showGallery(@PathVariable Long id, Model model,@AuthUser User user,
	          HttpSession session) {
	      model.addAttribute("userName",user.getName());
	      session.setAttribute("roomId", id);
		Room room = roomRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + id));

		// 이미지 경로를 최신 순으로 Gallery 엔티티에서 가져오기
		List<Gallery> galleries = galleryRep.findByRoomIdOrderByLastdateDesc(id);
		List<String> imageUrls = galleries.stream().flatMap(gallery -> gallery.getImagePaths().stream())
				.collect(Collectors.toList());

		model.addAttribute("room", room);
		model.addAttribute("imageUrls", imageUrls); // 이미지 URL 리스트를 모델에 추가

		return "gallery"; // 갤러리 페이지의 Thymeleaf 템플릿 이름을 반환합니다.
	}

	@GetMapping("/calculator/{id}")
	public String Calculator(@PathVariable Long id, Model model, 
			@ModelAttribute("oneLineLog") String oneLineLog,@AuthUser User user,
	          HttpSession session) {
	      model.addAttribute("userName",user.getName());
	      session.setAttribute("roomId", id);
		Room room = roomRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + id));
		model.addAttribute("room", room);

		List<Calculator> calculators = calculatorRep.findByRoomId(id);

		// 로그 저장
		if (oneLineLog != null) {
			priceLogLists.add(oneLineLog);
		}

		model.addAttribute("priceLogList", priceLogLists);
		model.addAttribute("calculator", calculators);

		return "calculator";
	}

}
