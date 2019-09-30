package com.mainacad.controller;

import com.mainacad.entity.Item;
import com.mainacad.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
  public String getItemsPage(Model model, @ModelAttribute("items") List<Item> items) {
    items = itemService.findAll();
    model.addAttribute("items", items);

    return "items";
  }
}
