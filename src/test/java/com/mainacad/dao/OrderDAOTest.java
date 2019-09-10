package com.mainacad.dao;

import com.mainacad.App;
import com.mainacad.dao.connection.ConnectionFactory;
import com.mainacad.model.Cart;
import com.mainacad.model.Item;
import com.mainacad.model.Order;
import com.mainacad.model.User;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(App.class)
@ActiveProfiles("test")
class OrderDAOTest {
  private static List<Cart> carts = new ArrayList<>();
  private static List<User> users = new ArrayList<>();
  private static List<Item> items = new ArrayList<>();
  private static List<Order> orders = new ArrayList<>();
  private static final Long NEW_CREATION_TIME = 1565024869119L;

  @Autowired
  private ConnectionFactory connectionFactory;

  @Autowired
  private CartDAO cartDAO;

  @Autowired
  private OrderDAO orderDAO;

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

    Order order = new Order(item, 2, cart);
    id = (Integer) session.save(order);
    order.setId(id);
    orders.add(order);

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
    Order checkedOrder = new Order(items.get(0), 1, carts.get(0));
    checkedOrder = orderDAO.save(checkedOrder);
    orders.add(checkedOrder);

    assertNotNull(checkedOrder);
    assertNotNull(checkedOrder.getId());

    Integer chechedOrderId = checkedOrder.getId();
    checkedOrder = orderDAO.findById(chechedOrderId);
    assertNotNull(checkedOrder);
    assertEquals(chechedOrderId, checkedOrder.getId());
  }

  @Test
  void testDelete() {
    orderDAO.delete(orders.get(0));

    Cart checkedOrder = cartDAO.findById(orders.get(0).getId());
    assertNull(checkedOrder);

    orders.remove(0);
  }

  @Test
  void testFindById() {
    Order checkedOrders = orderDAO.findById(orders.get(0).getId());
    assertNotNull(checkedOrders);
    assertEquals(orders.get(0).getId(), checkedOrders.getId());
  }

  @Test
  void testUpdate() {
    Order order = orders.get(0);
    order.setAmount(3);
    Order checkedOrder = orderDAO.update(order);
    assertNotNull(order, "Update method return null object");
    assertEquals(3, checkedOrder.getAmount());

    checkedOrder = orderDAO.findById(checkedOrder.getId());
    assertEquals(3, checkedOrder.getAmount());
  }

  @Test
  void testFindByCart() {
    List<Order> checkedOrders = orderDAO.findByCart(carts.get(0));
    assertNotNull(checkedOrders);
    assertFalse(checkedOrders.isEmpty());

    orderDAO.delete(orders.get(0));
    orders.clear();

    checkedOrders = orderDAO.findByCart(carts.get(0));
    assertNotNull(checkedOrders);
    assertTrue(checkedOrders.isEmpty());
  }

  @Test
  void findClosedOrdersByUserAndPeriod() {
    User user = users.get(0);
    Cart cart = carts.get(0);
    Order order = orders.get(0);

    Long from = cart.getCreationTime() - 1000;
    Long to = cart.getCreationTime() + 1000;

    List<Order> checkedOrdersList = orderDAO.findClosedOrdersByUserAndPeriod(user, from, to);
    assertNotNull(checkedOrdersList, "findClosedOrdersByUserAndPeriod method return null object");

    Optional<Order> optionalCheckedOrder = checkedOrdersList.stream()
            .peek(orderElement -> {
              Cart checkedCart = cartDAO.findById(orderElement.getCart().getId());
              assertEquals(user.getId(), checkedCart.getUser().getId()); // check that found ordеr with correct user
              assertEquals(true, checkedCart.getClosed());// check that found closed ordеr
            })
            .filter(element -> element.getId().equals(order.getId()))
            .findAny();

    assertNull(optionalCheckedOrder.orElse(null), "Test order found, but it shouldn't"); // we haven't closed orders

    cart.setClosed(true);
    cartDAO.update(cart);

    checkedOrdersList = orderDAO.findClosedOrdersByUserAndPeriod(user, from, to);

    optionalCheckedOrder = checkedOrdersList.stream()
            .peek(orderElement -> {
              Cart checkedCart = cartDAO.findById(orderElement.getCart().getId());
              assertEquals(user.getId(), checkedCart.getUser().getId()); // check that found ordеr with correct user
              assertEquals(true, checkedCart.getClosed());// check that found closed ordеr
            })
            .filter(element -> element.getId().equals(order.getId()))
            .findAny();
    assertNotNull(optionalCheckedOrder.orElse(null), "Test order not found, but it should"); // we haven`t closed orders

    from = cart.getCreationTime() + 500;

    checkedOrdersList = orderDAO.findClosedOrdersByUserAndPeriod(user, from, to);

    optionalCheckedOrder = checkedOrdersList.stream()
            .peek(orderElement -> {
              Cart checkedCart = cartDAO.findById(orderElement.getCart().getId());
              assertEquals(user.getId(), checkedCart.getUser().getId()); // check that found ordеr with correct user
              assertEquals(true, checkedCart.getClosed());// check that found closed ordеr
            })
            .filter(element -> element.getId().equals(order.getId()))
            .findAny();

    assertNull(optionalCheckedOrder.orElse(null), "Test order found, but it shouldn't"); // we haven't closed orders within that period
  }

  @Test
  void testGetSumOfAllOrdersByUserIdAndPeriod() {
    User user = users.get(0);
    Cart cart = carts.get(0);
    Order order = orders.get(0);

    Long from = cart.getCreationTime() - 1000;
    Long to = cart.getCreationTime() + 1000;

    Integer checkedSum = orderDAO.getSumOfAllOrdersByUserIdAndPeriod(user, from, to);
    assertNotNull(checkedSum);
    assertEquals(0, checkedSum);

    cart.setClosed(true);
    cartDAO.update(cart);

    checkedSum = orderDAO.getSumOfAllOrdersByUserIdAndPeriod(user, from, to);
    assertEquals(2800000, checkedSum);

    Order newOrder = new Order(order.getItem(), 3, order.getCart());
    newOrder = orderDAO.save(newOrder);
    orders.add(newOrder);

    checkedSum = orderDAO.getSumOfAllOrdersByUserIdAndPeriod(user, from, to);
    assertNotNull(checkedSum);
    assertEquals(7000000, checkedSum);
  }
}