package org.project.pack.repository;

import org.project.pack.entity.Gallery;
import org.project.pack.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long>{
	 List<Gallery> findByRoomId(Long roomId);
	 public void deleteAllByroom_id(Long id);
	// 최신 순으로 갤러리를 불러오는 쿼리
	    @Query("SELECT g FROM Gallery g WHERE g.room.id = :roomId ORDER BY g.lastdate DESC")
	    List<Gallery> findByRoomIdOrderByLastdateDesc(@Param("roomId") Long roomId);
}
