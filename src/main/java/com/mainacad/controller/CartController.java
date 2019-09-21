package com.mainacad.controller;

import com.mainacad.entity.Cart;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
  @Autowired
  CartService cartService;

  @Autowired
  OrderService orderService;

  @GetMapping(path="/get-by-id/{id}")
  public ResponseEntity<Cart> getById(@PathVariable Integer id) {
    Cart cartFromDB = cartService.findById(id);
    if (cartFromDB != null){
      return new ResponseEntity(cartFromDB, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @GetMapping(path="/get-sum-by-id/{id}")
  public ResponseEntity<Integer> getSumById(@PathVariable Integer id) {
    Cart cartFromDB = cartService.findById(id);
    if (cartFromDB != null){
      return new ResponseEntity(cartService.getCartSum(cartFromDB), HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @PutMapping(path="/close/{id}")
  public ResponseEntity<Cart> closeCart(@PathVariable Integer id) {
    Cart cartFromDB = cartService.findById(id);
    if (cartFromDB != null){
      cartFromDB = cartService.close(cartFromDB);
      return new ResponseEntity(cartFromDB, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @PostMapping()
  public ResponseEntity<Cart> createCart(@RequestBody User user) {
    Cart openCart = cartService.createCartForUser(user);

    if (openCart != null) {
      return new ResponseEntity<Cart>(openCart, HttpStatus.OK);
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @DeleteMapping(path="/{id}")
  public ResponseEntity delete(@PathVariable Integer id) {
    Cart cartToDelete = cartService.findById(id);
    if (cartToDelete == null) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    } else {
      List<Order> ordersToDelete = orderService.getOrdersByCart(cartToDelete);
      ordersToDelete.forEach(order -> orderService.deleteOrder(order));

      cartService.deleteCart(cartToDelete);
      return new ResponseEntity(HttpStatus.OK);
    }
  }

}
