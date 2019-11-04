package com.mainacad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mainacad.entity.User;
import com.mainacad.service.ItemService;
import com.mainacad.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(SpringRunner.class)
@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {
  private MockMvc mockMvc;

  @Autowired
  private RegistrationController registrationController;

  @MockBean
  UserService userService;

  @BeforeEach
  void setUp() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/jsp/");
    viewResolver.setSuffix(".jsp");

    MockitoAnnotations.initMocks(this);

    mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
            .setViewResolvers(viewResolver)
            .build();
  }

  @Test
  void getRegistrationPage() throws Exception {
    String requestUrl = "/register";
    this.mockMvc.perform(get(requestUrl)).andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("registration"))
            .andExpect(forwardedUrl("/WEB-INF/jsp/registration.jsp"));
  }

  @Test
  void checkUserExistByLogin() throws Exception {
    String requestUrl = "/registration/userExist";
    String existedUserLogin = "user1";
    String nonExistedUserLogin = "user2";

    User existingUser = new User(1, existedUserLogin, "pwd", "firstname", "secondName");

    Mockito.when(userService.findByLogin(existedUserLogin)).thenReturn(existingUser);
    Mockito.when(userService.findByLogin(nonExistedUserLogin)).thenReturn(null);

    MvcResult result = mockMvc.perform(get(requestUrl)
            .param("login", existedUserLogin)).andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    ObjectMapper objectMapper = new ObjectMapper();
    Map responseData = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

    assertEquals("LinkedHashMap", responseData.getClass().getSimpleName());
    assertNotNull(responseData.get("userExist"));
    assertEquals(true, responseData.get("userExist"));

    result = mockMvc.perform(get(requestUrl)
            .param("login", nonExistedUserLogin)).andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    responseData = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

    assertEquals("LinkedHashMap", responseData.getClass().getSimpleName());
    assertNotNull(responseData.get("userExist"));
    assertEquals(false, responseData.get("userExist"));

    Mockito.verify(userService, Mockito.times(1)).findByLogin(existedUserLogin);
    Mockito.verify(userService, Mockito.times(1)).findByLogin(nonExistedUserLogin);
  }

  @Test
  void registerUser() throws Exception {
    String requestUrl = "/register";
    String action = "register";
    String wrongAction = "register1";
    String login = "login";
    String password = "password";
    String fname = "fname";
    String lname = "lname";

    // test required fields

    // action parameter missing
    mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("login", login)
            .param("password", password)
            .param("fname", fname)
            .param("lname", lname))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(view().name(""));

    // set wrong action parameter
    mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", wrongAction)
            .param("login", login)
            .param("password", password)
            .param("fname", fname)
            .param("lname", lname))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(view().name(""));

    // login parameter missing
    mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", action)
            .param("password", password)
            .param("fname", fname)
            .param("lname", lname))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(view().name(""));

    // password parameter missing
    mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", action)
            .param("login", login)
            .param("fname", fname)
            .param("lname", lname))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(view().name(""));

    // firstName parameter missing
    mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", action)
            .param("login", login)
            .param("password", password)
            .param("lname", lname))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(view().name(""));

    // secondName parameter missing
    mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", action)
            .param("login", login)
            .param("password", password)
            .param("fname", fname))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(view().name(""));

    // user already exists
    User user = new User(login, password, fname, lname);
    Mockito.when(userService.save(user)).thenReturn(null);
    mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", action)
            .param("login", login)
            .param("password", password)
            .param("fname", fname)
            .param("lname", lname))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(view().name(""));

    Mockito.verify(userService, Mockito.times(1)).save(user);

    // user not exists and created
    Mockito.reset(userService);

    Mockito.when(userService.save(user)).thenReturn(user);
    mockMvc.perform(post(requestUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("action", action)
            .param("login", login)
            .param("password", password)
            .param("fname", fname)
            .param("lname", lname))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/*"))
            .andExpect(request().sessionAttribute("user", is(user)))
            .andExpect(model().attribute("userCreated", true))
            .andExpect(view().name("redirect:/"));

    Mockito.verify(userService, Mockito.times(1)).save(user);
  }
}