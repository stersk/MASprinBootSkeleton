package com.mainacad.controller.web;

import com.mainacad.controller.web.AuthController;
import com.mainacad.entity.User;
import com.mainacad.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@ActiveProfiles("web")
class AuthControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  void testLogout() throws Exception {
    HashMap<String, Object> sessionAttributes = new HashMap<>();
    User checkedUser = new User();
    sessionAttributes.put("user", checkedUser);
    sessionAttributes.put("secondTestUser", 1);

    this.mockMvc.perform(get("/logout").sessionAttrs(sessionAttributes)).andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(forwardedUrl("index"))
            .andExpect(model().attribute("user", nullValue()));
  }

  @Test
  void getLandingPage() throws Exception {
    String requestUrl = "/";
//     No auth and no wrongAuth attr.
    this.mockMvc.perform(get(requestUrl)).andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(forwardedUrl("index"))
            .andExpect(model().attribute("wrongAuth", is(false)));

//  Auth exist, redirect to Items list
    HashMap<String, Object> sessionAttributes = new HashMap<>();
    User checkedUser = new User();
    sessionAttributes.put("user", checkedUser);

    this.mockMvc.perform(get(requestUrl).sessionAttrs(sessionAttributes)).andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/items"))
            .andExpect(redirectedUrlPattern("/items*"))
            .andExpect(model().attribute("wrongAuth", is(false)));
  }

  @Test
  void authUser() throws Exception {
    String url = "/auth";

    User checkedUser = new User();
    checkedUser.setId(1);

    Mockito.when(userService.findByLoginAndPassword("login", "password")).thenReturn(null);
    Mockito.when(userService.findByLoginAndPassword("login", "pwd")).thenReturn(checkedUser);

    // wrong action parameter
    mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", "act")
    )
      .andExpect(status().isOk())
      .andExpect(view().name("/"))
      .andExpect(forwardedUrl("/"))
      .andExpect(model().attribute("wrongAuth", is(true)));

    // wrong auth
    mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", "login")
            .param("login", "login")
            .param("password", "password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/"))
            .andExpect(redirectedUrlPattern("/*"))
            .andExpect(model().attribute("wrongAuth", is(true)));

    // auth successful
    mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", "login")
            .param("login", "login")
            .param("password", "pwd")    )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/items"))
            .andExpect(redirectedUrl("/items"))
            .andExpect(model().attribute("user", is(checkedUser)))
            .andExpect(model().attribute("wrongAuth", nullValue()));

    Mockito.verify(userService, Mockito.times(2)).findByLoginAndPassword(Mockito.anyString(), Mockito.anyString());
  }
}