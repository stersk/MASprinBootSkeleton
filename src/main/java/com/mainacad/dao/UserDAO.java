package com.mainacad.dao;

import com.mainacad.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Integer> {

  List<User> findAllByLoginAndPassword(String login, String password);

  @Query(nativeQuery = true, value = "SELECT * FROM users WHERE login=:login")
  List<User> findAllByLogin(@Param("login") String login);
}
