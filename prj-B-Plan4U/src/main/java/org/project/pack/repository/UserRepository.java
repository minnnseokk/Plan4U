package org.project.pack.repository;

import java.util.List;

import org.project.pack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	public User findByEmail(String email);
	public User findByName(String name);
	public List<User> findAllByNameLike(String name);
}
