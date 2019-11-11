package com.mainacad.service;

import com.mainacad.ApplicationRunner;
import com.mainacad.dao.UserDAO;
import com.mainacad.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(ApplicationRunner.class)
@ActiveProfiles("test")
class UserServiceTest {
  private static List<User> users = new ArrayList<>();
  private static final String USER_LOGIN = "user_login";
  private static final String USER_PASSWORD = "test_pass";

  @Autowired
  UserDAO userDAO;

  @Autowired
  UserService userService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    User user = new User(USER_LOGIN, passwordEncoder.encode(USER_PASSWORD), "test_name", "test_surname");
    user = userDAO.save(user);
    users.add(user);
  }

  @AfterEach
  void tearDown() {
    users.forEach(user -> userDAO.delete(user));
    users.clear();
  }

  @Test
  void testFindByLoginAndPassword() {
    User checkedUser = userService.findByLoginAndPassword(USER_LOGIN, USER_PASSWORD);
    assertNotNull(checkedUser);
    assertEquals(users.get(0), checkedUser);

    checkedUser = userService.findByLoginAndPassword(USER_LOGIN + "_", USER_PASSWORD);
    if (checkedUser != null) {
      assertNotEquals(users.get(0), checkedUser);
    }

    checkedUser = userService.findByLoginAndPassword(USER_LOGIN, USER_PASSWORD + "_");
    if (checkedUser != null) {
      assertNotEquals(users.get(0), checkedUser);
    }
  }

  @Test
  void testFindByLogin() {
    User checkedUser = userService.findByLogin(USER_LOGIN);
    assertNotNull(checkedUser);
    assertEquals(users.get(0), checkedUser);

    checkedUser = userService.findByLogin(USER_LOGIN + "_");
    if (checkedUser != null) {
      assertNotEquals(users.get(0), checkedUser);
    }
  }

  @Test
  void testSave() {
    User user = new User(USER_LOGIN + "1", USER_PASSWORD, "test_name", "test_surname");
    users.add(user);

    User checkedUser = userService.save(user);
    assertNotNull(checkedUser);

    checkedUser = userService.save(users.get(0));
    assertNull(checkedUser);
  }

  @Test
  void testFindById() {
    User checkedUser = userService.findById(users.get(0).getId());

    assertNotNull(checkedUser);
    assertEquals(users.get(0), checkedUser);

    userService.delete(checkedUser.getId());

    checkedUser = userService.findById(users.get(0).getId());
    assertNull(checkedUser);
  }

  @Test
  void testUpdate() {
    String newName = "New name";
    User checkedUser = users.get(0);

    checkedUser.setFirstName(newName);
    checkedUser = userService.update(checkedUser);
    assertNotNull(checkedUser);

    checkedUser = userService.findById(users.get(0).getId());
    assertNotNull(checkedUser);
    assertEquals(newName, checkedUser.getFirstName());

    userService.delete(checkedUser.getId());

    checkedUser = userService.update(users.get(0));
    assertNull(checkedUser);
  }

  @Test
  void testDelete() {
    User checkedUser = userService.findById(users.get(0).getId());
    assertNotNull(checkedUser);
    assertEquals(users.get(0), checkedUser);

    userService.delete(checkedUser.getId());

    checkedUser = userService.findById(users.get(0).getId());
    assertNull(checkedUser);
  }
}