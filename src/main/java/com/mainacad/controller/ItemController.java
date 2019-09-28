package com.mainacad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mainacad.entity.Item;
import com.mainacad.entity.User;
import com.mainacad.service.ItemService;
import com.mainacad.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes({"user", "items"})
public class ItemController {
  @Autowired
  ItemService itemService;

  @ModelAttribute
  public void addAttributes(Model model) {
    if (!model.containsAttribute("items")) {
      model.addAttribute("items", new ArrayList<Item>());
    }
  }

  @GetMapping("/items")
  @Profile("dev")
  public String getLandingPage(Model model, @ModelAttribute("items") List<Item> items) {
    items = itemService.findAll();
    model.addAttribute("items", items);

    return "items";
  }
}
