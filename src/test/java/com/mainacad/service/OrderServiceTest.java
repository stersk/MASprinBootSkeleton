package com.mainacad.service;

import com.mainacad.ApplicationRunner;
import com.mainacad.dao.CartDAO;
import com.mainacad.dao.ItemDAO;
import com.mainacad.dao.OrderDAO;
import com.mainacad.dao.UserDAO;
import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(ApplicationRunner.class)
@ActiveProfiles("test")
class OrderServiceTest {
  @Autowired
  private ItemDAO itemDAO;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private OrderDAO orderDAO;

  @Autowired
  private CartDAO cartDAO;

  @Autowired
  private OrderService orderService;

  private static List<User> users = new ArrayList<>();
  private static List<Item> items = new ArrayList<>();
  private static List<Cart> carts = new ArrayList<>();
  private static List<Order> orders = new ArrayList<>();

  @BeforeEach
  void setUp() {
    // crete test item
    Item item = new Item("test_item", "Test item", 20000);
    item = itemDAO.save(item);
    items.add(item);

    // create test user
    User user = new User("user_login", "test_pass", "test_name", "test_surname");
    user = userDAO.save(user);
    users.add(user);

    // create test cart
    Cart cart = new Cart(1565024867119L, false, user);
    cart = cartDAO.save(cart);
    carts.add(cart);
  }

  @AfterEach
  void tearDown() {
    orders.forEach(order -> orderDAO.delete(order));
    orders.clear();

    carts.forEach(cart -> cartDAO.delete(cart));
    carts.clear();

    items.forEach(item -> itemDAO.delete(item));
    items.clear();

    users.forEach(user -> userDAO.delete(user));
    users.clear();
  }

  @Test
  void testCreateOrderByItemAndUser() {
    Order checkedOrder = orderService.createOrderByItemAndUser(items.get(0), 2, users.get(0));
    orders.add(checkedOrder);

    assertNotNull(checkedOrder);
    assertNotNull(checkedOrder.getId());

    assertEquals(items.get(0), checkedOrder.getItem());
    assertEquals(2, checkedOrder.getAmount());

    Cart checkedCart = checkedOrder.getCart();
    assertNotNull(checkedCart);
    assertEquals(carts.get(0), checkedCart);
    assertEquals(users.get(0), checkedCart.getUser());
    assertEquals(false, checkedCart.getClosed());
  }

  @Test
  void testAddItemToOrder() {
    Order createdOrder = orderService.addItemToOrder(items.get(0),users.get(0));
    orders.add(createdOrder);

    assertNotNull(createdOrder);

    Order checkedOrder = orderService.findById(createdOrder.getId());
    assertEquals(1, checkedOrder.getAmount());

    createdOrder = orderService.addItemToOrder(items.get(0),users.get(0));

    checkedOrder = orderService.findById(createdOrder.getId());
    assertEquals(2, checkedOrder.getAmount());
  }
}