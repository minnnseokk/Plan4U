package org.project.pack.repository;

import java.util.List;

import org.project.pack.entity.Calculator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculatorRepository extends JpaRepository<Calculator, Long> {	
	List<Calculator> findByRoomId(Long roomId);
	public void deleteAllByroom_id(Long id);
}
