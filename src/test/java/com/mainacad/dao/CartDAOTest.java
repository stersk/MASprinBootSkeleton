package com.mainacad.dao;

import com.mainacad.App;
import com.mainacad.dao.connection.ConnectionFactory;
import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
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

@SpringJUnitConfig(App.class)
@ActiveProfiles("test")
class CartDAOTest {
  @Autowired
  private ConnectionFactory connectionFactory;

  @Autowired
  private CartDAO cartDAO;

  @Autowired
  private OrderDAO orderDAO;

  private static List<Cart> carts = new ArrayList<>();
  private static List<User> users = new ArrayList<>();
  private static List<Item> items = new ArrayList<>();
  private static List<Order> orders = new ArrayList<>();
  private static final Long NEW_CREATION_TIME = 1565024869119L;

  @BeforeEach
  void setUp() {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();

    session.getTransaction().begin();

    User user = new User("test_user_login", "test_pass", "test_name", "test_surname");
    Integer id = (Integer) session.save(user);
    user.setId(id);
    users.add(user);

    Item item = new Item("testCode", "Kellys Spider 40 (2014)", 1400000);
    id = (Integer) session.save(item);
    item.setId(id);
    items.add(item);

    Cart cart = new Cart(1565024867119L, false, user);
    id = (Integer) session.save(cart);
    cart.setId(id);
    carts.add(cart);

    session.getTransaction().commit();
    session.close();
  }

  @AfterEach
  void tearDown() {
    SessionFactory sessionFactory = connectionFactory.getSessionFactory();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();

    for (Order order: orders) {
      if (order.getId() != null) {
        session.delete(order);
      }
    }
    orders.clear();

    for (Cart cart: carts) {
      if (cart.getId() != null) {
        session.delete(cart);
      }
    }
    carts.clear();

    for (Item item: items) {
      if (item.getId() != null) {
        session.delete(item);
      }
    }
    items.clear();

    for (User user: users) {
      if (user.getId() != null) {
        session.delete(user);
      }
    }
    users.clear();

    session.getTransaction().commit();
    session.close();
  }

  @Test
  void testSave() {
    Cart checkedCart = new Cart(1565024867119L, false, users.get(0));
    checkedCart = cartDAO.save(checkedCart);
    carts.add(checkedCart);

    assertNotNull(checkedCart);
    assertNotNull(checkedCart.getId());

    Integer chechedCartId = checkedCart.getId();
    checkedCart = cartDAO.findById(chechedCartId);
    assertNotNull(checkedCart);
    assertEquals(chechedCartId, checkedCart.getId());
  }

  @Test
  void testUpdate() {
    Cart checkedCart = carts.get(0);
    assertNotEquals(checkedCart.getCreationTime(), NEW_CREATION_TIME);

    checkedCart.setCreationTime(NEW_CREATION_TIME);
    cartDAO.update(checkedCart);

    checkedCart = cartDAO.findById(checkedCart.getId());
    assertNotNull(checkedCart);
    assertEquals(checkedCart.getCreationTime(), NEW_CREATION_TIME);
  }

  @Test
  void testFindById() {
    Cart checkedCart = cartDAO.findById(carts.get(0).getId());
    assertNotNull(checkedCart);
    assertEquals(carts.get(0).getId(), checkedCart.getId());
  }

  @Test
  void testDelete() {
    cartDAO.delete(carts.get(0));

    Cart checkedCart = cartDAO.findById(carts.get(0).getId());
    assertNull(checkedCart);

    carts.remove(0);
  }

  @Test
  void testFindByUser() {
    List<Cart> checkedCarts = cartDAO.findByUser(users.get(0));
    assertNotNull(checkedCarts);
    assertFalse(checkedCarts.isEmpty());

    cartDAO.delete(carts.get(0));
    carts.clear();

    checkedCarts = cartDAO.findByUser(users.get(0));
    assertNotNull(checkedCarts);
    assertTrue(checkedCarts.isEmpty());
  }

  @Test
  void testFindOpenCartByUser() {
    Cart checkedCart = cartDAO.findOpenCartByUser(users.get(0));
    assertNotNull(checkedCart);

    checkedCart.setClosed(true);
    cartDAO.update(checkedCart);

    checkedCart = cartDAO.findOpenCartByUser(users.get(0));
    assertNull(checkedCart);
  }

  @Test
  void testGetCartSum() {
    Order firstOrder = new Order(items.get(0), 1, carts.get(0));
    firstOrder = orderDAO.save(firstOrder);
    orders.add(firstOrder);

    Order secondOrder = new Order(items.get(0), 2, carts.get(0));
    secondOrder = orderDAO.save(secondOrder);
    orders.add(secondOrder);

    Integer checkedSum = cartDAO.getCartSum(carts.get(0));
    assertNotNull(checkedSum);
    assertEquals(4200000, checkedSum);

    orderDAO.delete(firstOrder);
    orderDAO.delete(secondOrder);

    checkedSum = cartDAO.getCartSum(carts.get(0));
    assertNull(checkedSum);

    orders.clear();
  }
}