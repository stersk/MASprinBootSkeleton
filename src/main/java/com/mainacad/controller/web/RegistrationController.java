package com.mainacad.controller.web;

import com.mainacad.entity.User;
import com.mainacad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@Profile("web")
public class RegistrationController {
  @Autowired
  UserService userService;

  @Autowired()
  PasswordEncoder passwordEncoder;

  @GetMapping("/register")
  public String getRegistrationPage(Model model) {
    return "registration";
  }

  @GetMapping(value = "/registration/userExist", produces = "application/json")
  @ResponseBody
  public Map<String, Boolean> checkUserExistByLogin(Model model, @RequestParam(name = "login") String userLogin) {
    User user = userService.findByLogin(userLogin);

    Map<String, Boolean> map = new HashMap<>();
    map.put("userExist", user != null);

    return map;
  }

  @PostMapping(path = "/register")
  public ModelAndView registerUser(Model model, HttpSession session, @RequestBody MultiValueMap<String, String> queryData) {
    ModelAndView respond = new ModelAndView();
    String action = queryData.getFirst("action");

    if (action != null && action.equals("register")) {
      String login = queryData.getFirst("login");
      String password = queryData.getFirst("password");
      String firstName = queryData.getFirst("fname");
      String lastName = queryData.getFirst("lname");

      if (login == null || password == null || firstName == null || lastName == null) {
        respond.setViewName("");
        respond.setStatus(HttpStatus.BAD_REQUEST);

        return respond;
      }

      User user = new User(login, passwordEncoder.encode(password), firstName, lastName);

      User savedUser = userService.save(user);

      if (savedUser != null) {
        session.setAttribute("user", user);
        model.addAttribute("userCreated", true);

        respond = new ModelAndView("redirect:/", (Map<String, ?>) model);
      } else {

        respond.setStatus(HttpStatus.BAD_REQUEST);
        respond.setViewName("");
      }

    } else {
      respond.setViewName("");
      respond.setStatus(HttpStatus.BAD_REQUEST);
    }

    return respond;
  }
}
