package com.mainacad.controller;

import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.ItemService;
import com.mainacad.service.OrderService;
import com.mainacad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderRestController {
  @Autowired
  OrderService orderService;

  @Autowired
  CartService cartService;

  @Autowired
  UserService userService;

  @Autowired
  ItemService itemService;

  @GetMapping(path="/get-by-id/{id}")
  public ResponseEntity<Cart> getById(@PathVariable Integer id) {
    Order orderFromDB = orderService.findById(id);
    if (orderFromDB != null){
      return new ResponseEntity(orderFromDB, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @GetMapping(path="/get-orders-by-card/{id}")
  public ResponseEntity<Cart> getOrdersByCartId(@PathVariable Integer id) {
    Cart cart = cartService.findById(id);
    if (cart != null){
      return new ResponseEntity(orderService.getOrdersByCart(cart), HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @GetMapping(path="/get-opened-orders-by-user/{id}")
  public ResponseEntity<Cart> getOpenedOrdersByUser(@PathVariable Integer id) {
    User user = userService.findById(id);

    if (user != null){
      Cart openedCart = cartService.findOpenCartByUser(user);
      List<Order> ordersList = new ArrayList<>();
      if (openedCart != null) {
        ordersList = orderService.getOrdersByCart(openedCart);
      }
      return new ResponseEntity(ordersList, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @PutMapping(path="/add-item-to-order/{itemId}/{userId}")
  public ResponseEntity<Cart> addItemToOrder(@PathVariable Integer itemId, @PathVariable Integer userId) {
    Item item = itemService.findById(itemId);
    User user = userService.findById(userId);
    if (item != null && user != null){
      Order newOrder = orderService.addItemToOrder(item, user);

      return new ResponseEntity(newOrder, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @PutMapping(path="/update-order-amount/{orderId}/{newAmount}")
  public ResponseEntity<Order> updateOrderAmount(@PathVariable Integer orderId, @PathVariable Integer newAmount) {
    Order order = orderService.findById(orderId);
    if (order != null){
      if (newAmount == 0) {
        orderService.deleteOrder(order);
        order = null;
      } else {
        order = orderService.updateItemAmountInOrder(order, newAmount);
      }

      return new ResponseEntity(order, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @PutMapping(path="/remove-one-from-order/{orderId}")
  public ResponseEntity<Integer> removeOneFromOrder(@PathVariable Integer orderId) {
    Order order = orderService.findById(orderId);
    if (order != null){
      Integer newAmount = order.getAmount() - 1;
      if (newAmount == 0) {
        orderService.deleteOrder(order);
      } else {
        order.setAmount(newAmount);
        orderService.updateItemAmountInOrder(order, newAmount);
      }

      return new ResponseEntity(newAmount, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @DeleteMapping(path="/{id}")
  public ResponseEntity delete(@PathVariable Integer id) {
    Order orderToDelete = orderService.findById(id);
    if (orderToDelete == null) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    } else {
      orderService.deleteOrder(orderToDelete);
      return new ResponseEntity(HttpStatus.OK);
    }
  }
}