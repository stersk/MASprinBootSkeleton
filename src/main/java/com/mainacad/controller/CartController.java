package com.mainacad.controller;

import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("user")
public class CartController {
  @Autowired
  CartService cartService;

  @GetMapping("/cart")
  @Profile("dev")
  public String getCartPage(Model model) {
    List<Order> orders = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    Integer cartSum = 0;
    User currentUser = null;

    Object userObject = model.asMap().get("user");
    if (userObject != null) {
      currentUser = (User) userObject;
      orders = cartService.getOrdersFromOpenCartByUser(currentUser);
      items = orders
              .stream()
              .parallel()
              .map(order -> order.getItem())
              .collect(Collectors.toList());

      Cart openCart = cartService.findOpenCartByUser(currentUser);
      if (openCart != null) {
        cartSum = cartService.getCartSum(openCart);
      }
    } else {
      return "redirect:/";
    }

    model.addAttribute("user", currentUser);
    model.addAttribute("orders", orders);
    model.addAttribute("items", items);
    model.addAttribute("cartSum", cartSum);

    return "cart";
  }
}
