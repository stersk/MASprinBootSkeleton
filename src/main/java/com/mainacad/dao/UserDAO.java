package com.mainacad.dao;

import com.mainacad.dao.connection.ConnectionFactory;
import com.mainacad.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAO {
  @Autowired
  private ConnectionFactory connectionFactory;

  public User save(User user) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    Integer id = (Integer) session.save(user);
    session.getTransaction().commit();
    session.close();

    user.setId(id);

    return user;
  }

  public User update(User user) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    session.update(user);
    session.getTransaction().commit();
    session.close();

    return user;
  }

  public User findById(Integer id) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    User user = session.find(User.class, id);
    session.getTransaction().commit();
    session.close();

    return user;
  }

  public List<User> findAll(){
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();
    String sql = "SELECT * FROM users";
    List<User> users = session.createNativeQuery(sql, User.class).getResultList();
    session.getTransaction().commit();
    session.close();

    return users;
  }

  public List<User> findByLogin(String login){
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();
    String sql = "SELECT * FROM users WHERE login=?";
    NativeQuery query = session.createNativeQuery(sql, User.class);
    query.setParameter(1, login);
    List<User> users = query.getResultList();
    session.getTransaction().commit();
    session.close();

    return users;
  }

  public void delete(User user){
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();

    User checkedUser = findById(user.getId());
    if (checkedUser != null) {
      session.delete(user);
    };

    session.getTransaction().commit();
    session.close();
  }
}
