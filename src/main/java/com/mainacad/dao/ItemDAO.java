package com.mainacad.dao;

import com.mainacad.dao.connection.ConnectionFactory;
import com.mainacad.model.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemDAO {
  @Autowired
  private ConnectionFactory connectionFactory;

  public Item save(Item item) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    Integer id = (Integer) session.save(item);
    session.getTransaction().commit();
    session.close();

    item.setId(id);

    return item;
  }

  public Item update(Item item) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    session.update(item);
    session.getTransaction().commit();
    session.close();

    return item;
  }

  public Item findById(Integer id) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    Item item = session.find(Item.class, id);
    session.getTransaction().commit();
    session.close();

    return item;
  }

  public List<Item> findAll(){
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();
    String sql = "SELECT * FROM items";
    List<Item> items = session.createNativeQuery(sql, Item.class).getResultList();
    session.getTransaction().commit();
    session.close();

    return items;
  }

  public List<Item> findByItemCode(String code) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();
    String sql = "SELECT * FROM items WHERE items.item_code = ?";

    NativeQuery nativeQuery = session.createNativeQuery(sql, Item.class);
    nativeQuery.setParameter(1, code);

    List<Item> items = nativeQuery.getResultList();
    session.getTransaction().commit();
    session.close();

    return items;
  }

  public void delete(Item item){
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();

    Item checkedItem = findById(item.getId());
    if (checkedItem != null) {
      session.delete(item);
    }

    session.getTransaction().commit();
    session.close();
  }
}
