package com.mainacad.dao;

import com.mainacad.App;
import com.mainacad.dao.connection.ConnectionFactory;
import com.mainacad.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig(App.class)
@ActiveProfiles("test")
class UserDAOTest {
  @Autowired
  private UserDAO userDAO;

  @Autowired
  private ConnectionFactory connectionFactory;

  private static List<User> users = new ArrayList<>();
  private static String TEST_USER_LOGIN = "test_user";

  @BeforeEach
  void setUp() {
    User user = new User(TEST_USER_LOGIN, "test_pass", "test_name", "test_surname");

    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    Integer id = (Integer) session.save(user);
    session.getTransaction().commit();
    session.close();

    user.setId(id);
    users.add(user);
  }

  @AfterEach
  void tearDown() {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();

    for (User user: users) {
      if (user.getId() != null) {
        session.delete(user);
      }
    }

    session.getTransaction().commit();
    session.close();

    users.clear();
  }

  @Test
  void testSaveAndFindOneAndDelete() {
    User user = new User("ignatenko2207", "123456", "Alex", "Ignatenko");

    User savedUser = userDAO.save(user);
    assertNotNull(savedUser);
    assertNotNull(savedUser.getId());

    User dbUser = userDAO.findById(savedUser.getId());
    assertNotNull(dbUser);

    userDAO.delete(savedUser);

    dbUser = userDAO.findById(savedUser.getId());
    assertNull(dbUser);
  }

  @Test
  void testSave() {
    User user = new User("ignatenko2207", "123456", "Alex", "Ignatenko");
    user = userDAO.save(user);

    users.add(user);

    assertNotNull(user.getId());

    User checkedItem = userDAO.findById(user.getId());
    assertNotNull(checkedItem);
    assertEquals(user.getId(), checkedItem.getId());
  }

  @Test
  void testUpdate() {
    User checkedUser = users.get(0);
    checkedUser.setPassword("new_password");
    checkedUser = userDAO.update(checkedUser);

    User checkedUserFromDB = userDAO.findById(checkedUser.getId());
    assertNotNull(checkedUserFromDB);
    assertEquals("new_password", checkedUserFromDB.getPassword());
  }

  @Test
  void testFindById() {
    User checkedUser = userDAO.findById(users.get(0).getId());
    assertNotNull(checkedUser);
    assertNotNull(checkedUser.getId());
    assertEquals(users.get(0).getId(), checkedUser.getId(), "Wrong user found");
  }

  @Test
  void testFindAll() {
    List<User> checkedUsers = userDAO.findAll();

    assertNotNull(checkedUsers);
    assertFalse(checkedUsers.isEmpty());
  }

  @Test
  void testDelete() {
    userDAO.delete(users.get(0));

    User checkedItem = userDAO.findById(users.get(0).getId());
    assertNull(checkedItem);

    users.remove(0);
  }
}
