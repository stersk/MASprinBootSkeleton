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

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(ApplicationRunner.class)
@ActiveProfiles("test")
class CartServiceTest {
  @Autowired
  private ItemDAO itemDAO;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private OrderDAO orderDAO;

  @Autowired
  private CartDAO cartDAO;

  @Autowired
  private CartService cartService;

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

    Order order = new Order(item, 3, cart);
    order = orderDAO.save(order);
    orders.add(order);
  }

  @AfterEach
  void tearDown() {
    orders.forEach(order -> {
      Order orderToDelete = orderDAO.findById(order.getId()).orElse(null);
      if (orderToDelete != null) {
        orderDAO.delete(order);
      }
    });
    orders.clear();

    carts.forEach(cart -> cartDAO.delete(cart));
    carts.clear();

    items.forEach(item -> itemDAO.delete(item));
    items.clear();

    users.forEach(user -> userDAO.delete(user));
    users.clear();
  }

  @Test
  void testCreateCartForUser() {
    Cart checkedCart = cartService.createCartForUser(users.get(0));
    carts.add(checkedCart);

    assertNotNull(checkedCart);
    assertEquals(false, checkedCart.getClosed());
    assertEquals(users.get(0), checkedCart.getUser());
  }

  @Test
  void testGetCartSum() {
    Integer checkedSum = cartService.getCartSum(carts.get(0));
    assertNotNull(checkedSum);
    assertEquals(60000, checkedSum);
  }

  @Test
  void testDeleteCart() {
    Cart createdCart = cartService.createCartForUser(users.get(0));
    carts.add(createdCart);

    Order createdOrder = orderService.addItemToOrder(items.get(0), users.get(0));
    orders.add(createdOrder);

    cartService.deleteCart(createdCart);

    Order checkedOrder = orderService.findById(createdOrder.getId());
    assertNull(checkedOrder);

    Cart checkedCart = cartService.findById(createdCart.getId());
    assertNull(checkedCart);
  }

  @Test
  void testGetOrdersFromOpenCartByUser() {
    Cart createdCart = cartService.createCartForUser(users.get(0));
    carts.add(createdCart);

    Order createdOrder = orderService.addItemToOrder(items.get(0), users.get(0));
    orders.add(createdOrder);

    List<Order> checkedOrders = cartService.getOrdersFromOpenCartByUser(users.get(0));
    assertNotNull(checkedOrders);
    assertEquals(1, checkedOrders.size());
    assertEquals(createdOrder.getId(), checkedOrders.get(0).getId());

    cartService.close(createdCart);
    checkedOrders = cartService.getOrdersFromOpenCartByUser(users.get(0));

    assertNotNull(checkedOrders);
    assertEquals(0, checkedOrders.size());
  }

  @Test
  void testFindById() {
    Cart checkedCart = cartService.findById(carts.get(0).getId());

    assertNotNull(checkedCart);
    assertEquals(carts.get(0), checkedCart);

    cartService.deleteCart(checkedCart);

    checkedCart = cartService.findById(carts.get(0).getId());
    assertNull(checkedCart);
  }

  @Test
  void testFindOpenCartByUser() {
    Cart checkedCart = cartService.findOpenCartByUser(users.get(0));
    assertNotNull(checkedCart);
    assertEquals(carts.get(0), checkedCart);

    User unknownUser = new User("secondUser", "password", "FirstName", "SecondName");
    userDAO.saveAndFlush(unknownUser);
    users.add(unknownUser);

    checkedCart = cartService.findOpenCartByUser(unknownUser);
    assertNull(checkedCart);

    cartService.close(carts.get(0));
    checkedCart = cartService.findOpenCartByUser(users.get(0));
    assertNull(checkedCart);
  }

  @Test
  void testClose() {
    Cart checkedCart = cartService.findById(carts.get(0).getId());
    assertFalse(checkedCart.getClosed());

    cartService.close(checkedCart);

    checkedCart = cartService.findById(carts.get(0).getId());
    assertTrue(checkedCart.getClosed());
  }
}