package org.project.pack.controller.api;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.project.pack.annotations.AuthUser;
import org.project.pack.classes.Randomizer;
import org.project.pack.entity.Gallery;
import org.project.pack.entity.Room;
import org.project.pack.entity.User;
import org.project.pack.repository.GalleryRepository;
import org.project.pack.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api")
public class GalleryController {

    @Autowired
    private RoomRepository roomRep;

    @Autowired
    private GalleryRepository galleryRep;

    @Autowired
    private FileIOController fileIOController; // FileIOController 사용

    @PostMapping("/gallery/{id}")
    public RedirectView uploadImages(
            @PathVariable Long id,
            @RequestPart("file") List<MultipartFile> files,
            @AuthUser User user) {

        Room room = roomRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));

        List<String> imageUrls = new ArrayList<>();
        String originalPath = "src/main/resources/static/galleryimage";

        for (MultipartFile file : files) {
            String ext = fileIOController.getExt(file.getOriginalFilename());
            if ("unknown".equals(ext)) {
                continue;
            }

            String randomName = Randomizer.generateString(10);
            String fullPath = originalPath + "/" + randomName + "." + ext;

            try {
                fileIOController.Save(file.getInputStream(), fullPath);
                String imageUrl = "/galleryimage/" + randomName + "." + ext;
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
                // 오류 발생 시, 오류 페이지로 리다이렉트
                return new RedirectView("/error"); 
            }
        }

        LocalDateTime now = LocalDateTime.now();
        Gallery gallery = new Gallery();
        gallery.setLastdate(now);
        gallery.setImagePaths(imageUrls);
        gallery.setRoom(room);
        gallery.setUser(user);
        
        galleryRep.save(gallery);

        // 저장된 갤러리 페이지로 리다이렉트
        return new RedirectView("/app/gallery/" + id);
    }
}
