package org.project.pack.repository;

import java.util.List;

import org.project.pack.entity.Guests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.project.pack.entity.User;
import org.project.pack.entity.Room;



@Repository
public interface GuestsRepository extends JpaRepository<Guests, Long>{
	public List<Guests> findByRoom(Room room);
	public List<Guests> findByRoom_id(Long id);
	public List<Guests> findByUser(User user);
	public void deleteAllByroom_id(Long id);
	
	void deleteByUserAndRoom(User user, Room room);
	
	@Query("SELECT COUNT(g) > 0 FROM Guests g WHERE g.room.id = :roomId AND g.user.id = :userId")
    boolean existsByRoom_IdAndUser_Id(@Param("roomId") Long roomId, @Param("userId") Long userId);
}
