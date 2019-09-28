package com.mainacad.controller;

import com.mainacad.entity.User;
import com.mainacad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@SessionAttributes({"wrongAuth", "user"})
public class AuthController {
  @Autowired
  UserService userService;

  @GetMapping("/")
  @Profile("dev")
  public ModelAndView getLandingPage(Model model) {
    if (!model.containsAttribute("wrongAuth")) {
      model.addAttribute("wrongAuth", false);
    }

    return new ModelAndView("redirect:/", (Map<String, ?>) model);
  }

  @GetMapping("/logout")
  @Profile("dev")
  public String getLogout(Model model, @ModelAttribute("user") User user) {
    model.addAttribute("user", null);

    return "index";
  }

  @PostMapping(path = "/auth")
  @Profile("dev")
  public ModelAndView authUser(Model model, HttpSession session, @RequestBody MultiValueMap<String, String> queryData) {
    boolean wrongAuth = false;
    ModelAndView respond = new ModelAndView("/");

    String action = queryData.getFirst("action");

    if (action.equals("login")) {
      String login = queryData.getFirst("login");
      String password = queryData.getFirst("password");

      User user = userService.findByLoginAndPassword(login, password);
      if (user != null) {
        wrongAuth = false;
        model.addAttribute("user", user);
        session.removeAttribute("wrongAuth");

        respond = new ModelAndView("redirect:/items", (Map<String, ?>) model);
      } else {
        wrongAuth = true;

        respond = new ModelAndView("redirect:/", (Map<String, ?>) model);
      }
    }

    // TODO Refactor from session attribute to flashAttribute or pass parameter via URL
    model.addAttribute("wrongAuth", wrongAuth);
    return respond;
  }
}