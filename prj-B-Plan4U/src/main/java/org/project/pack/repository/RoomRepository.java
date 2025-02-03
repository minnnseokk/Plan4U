package org.project.pack.repository;

import java.util.List;

import org.project.pack.entity.Room;
import org.project.pack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{
	public Room findByTitle(String title);
	
    // 호스트가 유저인 방 조회
    public List<Room> findByHost(User user);

    // 특정 방 ID 목록으로 방 조회
    public List<Room> findByIdIn(List<Long> ids);
    
    public void deleteById(Long id);
    @Query("SELECT COUNT(r) > 0 FROM Room r WHERE r.id = :roomId AND r.host.id = :hostId")
    boolean existsByIdAndHost_Id(@Param("roomId") Long roomId, @Param("hostId") Long hostId);
}