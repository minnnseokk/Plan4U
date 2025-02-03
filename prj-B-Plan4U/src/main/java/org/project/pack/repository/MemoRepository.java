package org.project.pack.repository;

import java.util.List;

import org.project.pack.entity.Gallery;
import org.project.pack.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long>{
	public void deleteAllByroom_id(Long id);
	List<Memo> findByRoomId(Long roomId);
}
