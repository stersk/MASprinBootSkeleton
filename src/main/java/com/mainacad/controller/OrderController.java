package com.mainacad.controller;

import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.ItemService;
import com.mainacad.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@SessionAttributes("user")
public class OrderController {
  @Autowired
  ItemService itemService;

  @Autowired
  OrderService orderService;

  @Autowired
  CartService cartService;

  @PostMapping(path = "/order/addItemToCart", produces = "application/json")
  @ResponseBody
  @Profile("dev")
  public ResponseEntity<Item> addItemToCart(Model model, HttpSession session, @RequestBody MultiValueMap<String, String> queryData) {
    User user = (User) model.asMap().get("user");
    if (user == null) {
       return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    String itemIdParameter = queryData.getFirst("itemId");
    if (itemIdParameter == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Item item = itemService.findById(Integer.parseInt(itemIdParameter));
    if (item == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Order order = orderService.addItemToOrder(item, user);

    if (order != null) {
      return new ResponseEntity<>(item, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value = "/order/removeFromOpenCart", produces = "application/json")
  @ResponseBody
  @Profile("dev")
  public Map<String, Object> removeFromOpenCart(Model model, @RequestBody MultiValueMap<String, String> queryData) {
    Integer orderId = Integer.parseInt(queryData.getFirst("orderId"));
    Integer newCartSum = deleteOrderAndReturnCartSum(orderId);

    Map<String, Object> map = new HashMap<>();
    map.put("cartSum", newCartSum);

    return map;
  }

  @PostMapping(value = "/order/updateItemAmountInOrder", produces = "application/json")
  @ResponseBody
  @Profile("dev")
  public Map<String, Object> updateItemAmountInOrder(Model model, @RequestBody MultiValueMap<String, String> queryData) {
    Integer orderId = Integer.parseInt(queryData.getFirst("orderId"));
    Integer amount = Integer.parseInt(queryData.getFirst("amount"));
    Integer newCartSum = 0;

    if (amount.equals(0)) {
      newCartSum = deleteOrderAndReturnCartSum(orderId);

    } else {
      Order updatedOrder = orderService.updateItemAmountInOrder(orderService.findById(orderId), amount);
      if (updatedOrder != null) {
        Cart cart = updatedOrder.getCart();
        newCartSum = cartService.getCartSum(cart);
      }
    }

    Map<String, Object> map = new HashMap<>();
    map.put("cartSum", newCartSum);

    return map;
  }

  private Integer deleteOrderAndReturnCartSum(Integer orderId) {
    Order orderToDelete = orderService.findById(orderId);
    Cart cartToEdit = orderToDelete.getCart();

    orderService.deleteOrder(orderToDelete);
    return cartService.getCartSum(cartToEdit);
  }
}
