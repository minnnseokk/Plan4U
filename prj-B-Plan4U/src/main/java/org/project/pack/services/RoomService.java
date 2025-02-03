package org.project.pack.services;

import org.project.pack.classes.Randomizer;
import org.project.pack.controller.api.FileIOController;
import org.project.pack.entity.Guests;
import org.project.pack.entity.Room;
import org.project.pack.entity.User;
import org.project.pack.repository.GuestsRepository;
import org.project.pack.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private FileIOController fileIOController; // 파일 처리용 컨트롤러

    @Autowired
    private RoomRepository roomRep;
    
    @Autowired
    private GuestsRepository guestsRep;

    Path pt = Paths.get("src/main/resources/static/roomimage");

    public String uploadRoomImage(MultipartFile file, Long roomId, HttpServletRequest req) {
        // 방 정보 조회
        Room room = roomRep.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + roomId));
        String imagePath;

        if (file == null || file.isEmpty()) {
            // 파일이 없을 때 기본 이미지 경로 설정
            imagePath = "/image/noimage.png";
        } else {
            // 파일이 있을 때 이미지 저장 처리
            String originalPath = pt.toString();
            String ext = fileIOController.getExt(file.getOriginalFilename());
            String randomName = Randomizer.generateString(10);
            String fullpath;

            if ("unknown".equals(ext)) {
                // 확장자가 unknown일 경우 기본 이미지 사용
                imagePath = "/image/noimage.png";
            } else {
                fullpath = originalPath + "/" + randomName + "." + ext;
                try {
                    fileIOController.Save(file.getInputStream(), Paths.get(fullpath));
                    imagePath = "/roomimage/" + randomName + "." + ext; // 이미지 경로 저장
                } catch (IOException e) {
                    e.printStackTrace(); // 실제로는 로깅 프레임워크를 사용해 예외를 기록하는 것이 좋습니다.
                    return "{\"message\":\"error\"}";
                }
            }
        }

        // 방에 이미지 경로 저장
        room.setImagePath(imagePath);
        roomRep.save(room);

        return "{\"message\":\"success\"}";
    }
    
    public List<User> getUsersByRoomId(Long roomId) {
        // Room 엔티티를 ID로 조회
        Room room = roomRep.findById(roomId).orElse(null);
        if (room == null) {
            return List.of(); // 또는 예외 처리
        }

        // 해당 Room에 연관된 Guests를 조회
        List<Guests> guests = guestsRep.findByRoom(room);

        // Guests를 통해 User를 추출
        return guests.stream()
                     .map(Guests::getUser) // Guests 엔티티에서 User 추출
                     .collect(Collectors.toList());
    }
}
