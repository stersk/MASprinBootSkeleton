package com.mainacad.service;

import com.mainacad.App;
import com.mainacad.dao.UserDAO;
import com.mainacad.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(App.class)
@ActiveProfiles("test")
class UserServiceTest {
  private static List<User> users = new ArrayList<>();
  private static final String USER_LOGIN = "user_login";
  private static final String USER_PASSWORD = "test_pass";

  @Autowired
  UserDAO userDAO;

  @Autowired
  UserService userService;


  @BeforeEach
  void setUp() {
    User user = new User(USER_LOGIN, USER_PASSWORD, "test_name", "test_surname");
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
  void findByLogin() {
    User checkedUser = userService.findByLogin(USER_LOGIN);
    assertNotNull(checkedUser);
    assertEquals(users.get(0), checkedUser);

    checkedUser = userService.findByLogin(USER_LOGIN + "_");
    if (checkedUser != null) {
      assertNotEquals(users.get(0), checkedUser);
    }
  }
}