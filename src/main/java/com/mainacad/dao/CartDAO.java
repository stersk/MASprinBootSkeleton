package com.mainacad.dao;

import com.mainacad.dao.connection.ConnectionFactory;
import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.User;
import com.mainacad.entity.Order;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

@Repository
public class CartDAO {

  @Autowired
  private ConnectionFactory connectionFactory;

  public Cart save(Cart cart) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    Integer id = (Integer) session.save(cart);
    session.getTransaction().commit();
    session.close();

    cart.setId(id);

    return cart;
  }

  public Cart update(Cart cart) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    session.update(cart);
    session.getTransaction().commit();
    session.close();

    return cart;
  }

  public Cart findById(Integer id) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    Cart cart = session.find(Cart.class, id);
    session.getTransaction().commit();
    session.close();

    return cart;
  }

  public List<Cart> findByUser(User user) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<Cart> criteriaQuery = builder.createQuery(Cart.class);
    Root<Cart> root = criteriaQuery.from(Cart.class);
    criteriaQuery.select(root).where(builder.equal(root.get("user"), user));

    Query<Cart> query = session.createQuery(criteriaQuery);
    List<Cart> carts = query.getResultList();

    session.getTransaction().commit();
    session.close();

    return carts;
  }

  public Cart findOpenCartByUser(User user) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<Cart> criteriaQuery = builder.createQuery(Cart.class);
    Root<Cart> root = criteriaQuery.from(Cart.class);

    criteriaQuery.select(root);

    Predicate cartByUser = builder.equal(root.get("user"), user);
    Predicate openCart = builder.equal(root.get("closed"), false);
    criteriaQuery.where(builder.and(cartByUser, openCart));

    Query<Cart> query = session.createQuery(criteriaQuery);
    Cart cart = query.stream().findFirst().orElse(null);

    session.getTransaction().commit();
    session.close();

    return cart;
  }

  public Integer getCartSum(Cart cart) {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<Number> criteriaQuery = builder.createQuery(Number.class);
    Root<Order> root = criteriaQuery.from(Order.class);
    Join<Order, Item> join = root.join("item", JoinType.LEFT);

    criteriaQuery.select(builder.sum(builder.prod(root.get("amount"), join.get("price"))).alias("sum"));
    criteriaQuery.where(builder.equal(root.get("cart"), cart));

    Query<Number> query = session.createQuery(criteriaQuery);
    Number sum = query.stream()
                       .map(summary -> Optional.ofNullable(summary))
                       .findFirst()
                       .orElse(null).orElse(null);

    session.getTransaction().commit();
    session.close();

    return sum == null ? null : sum.intValue();
  }

  public void delete(Cart cart){
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();

    Cart checkedCart = findById(cart.getId());
    if (checkedCart != null) {
      session.delete(checkedCart);
    }

    session.getTransaction().commit();
    session.close();
  }
}
