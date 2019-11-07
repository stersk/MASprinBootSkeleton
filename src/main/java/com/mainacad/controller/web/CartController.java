package com.mainacad.controller.web;

import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import com.mainacad.service.CartService;
import com.mainacad.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("user")
@Profile("web")
public class CartController {
  @Autowired
  CartService cartService;

  @Autowired
  OrderService orderService;

  @GetMapping("/cart")
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

  @PostMapping(path = "/cart/confirm", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ModelAndView confirmCart(Model model, HttpSession session) {
    if (model.asMap().get("user") != null) {
      Cart openedCart = cartService.findOpenCartByUser((User) model.asMap().get("user"));

      Integer itemsCount = orderService.getOrdersByCart(openedCart).size();
      Integer cartSum = cartService.getCartSum(openedCart);

      if (itemsCount > 0) {
        openedCart = cartService.close(openedCart);

        model.addAttribute("cart", openedCart);
        model.addAttribute("itemsCount", itemsCount);
        model.addAttribute("cartSum", cartSum);
        model.addAttribute("creationDate", new Date(openedCart.getCreationTime()));

        return new ModelAndView("cart-confirmed", model.asMap());
      } else {
        return new ModelAndView("redirect:/items");
      }
    } else {
      return new ModelAndView("redirect:/");
    }
  }

  @PostMapping(path = "/cart/discard", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ModelAndView discardCart(Model model, HttpSession session) {
    if (model.asMap().get("user") != null) {
      Cart openedCart = cartService.findOpenCartByUser((User) model.asMap().get("user"));
      cartService.deleteCart(openedCart);

      model.addAttribute("discardCompleted", true);
      return new ModelAndView("redirect:/items", model.asMap());
    } else {
      return new ModelAndView("redirect:/");
    }
  }
}
