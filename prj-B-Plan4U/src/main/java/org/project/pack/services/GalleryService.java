package org.project.pack.services;

import org.project.pack.entity.Gallery;
import org.project.pack.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRep;

    public List<Gallery> getSortedGalleries(Long roomId) {
        List<Gallery> galleries = galleryRep.findByRoomId(roomId);
        return galleries.stream()
                        .sorted((g1, g2) -> g2.getLastdate().compareTo(g1.getLastdate()))
                        .collect(Collectors.toList());
    }
}

