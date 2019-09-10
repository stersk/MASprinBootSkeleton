package com.mainacad.dao.connection;

import com.mainacad.App;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(App.class)
@ActiveProfiles("test")
class ConnectionFactoryTest {
  @Autowired
  ConnectionFactory connectionFactory;

  @Test
  void getSessionFactory() {
    Session session = connectionFactory.getSessionFactory().openSession();
    assertNotNull(session);
  }
}