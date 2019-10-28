package com.mainacad.controller;

import com.mainacad.ApplicationRunner;
import com.mainacad.entity.Item;
import com.mainacad.entity.User;
import com.mainacad.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;

@SpringJUnitConfig(ApplicationRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserRestControllerTest {

  @Autowired
  TestRestTemplate testRestTemplate;

  @MockBean
  UserService userService;

  @Test
  public void testCreateUser() throws URISyntaxException {
    User user = new User();
    user.setId(1);
    user.setFirstName("Tester");
    user.setLastName("User");
    user.setLogin("tester34");
    user.setPassword("12345");

    Mockito.when(userService.save(Mockito.any(User.class))).thenReturn(user);

    RequestEntity<User> request = new RequestEntity<>(user, HttpMethod.POST, new URI("/user"));
    ResponseEntity<User> response = testRestTemplate.exchange(request, User.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(userService, Mockito.times(1)).save(Mockito.any(User.class));
  }

  @Test
  public void testUpdateUser() throws URISyntaxException {
    User user = new User();
    user.setId(1);
    user.setFirstName("Test");
    user.setLastName("User");
    user.setLogin("tester");
    user.setPassword("12345");

    Mockito.when(userService.update(Mockito.any(User.class))).thenReturn(user);

    RequestEntity<User> request = new RequestEntity<>(user, HttpMethod.PUT, new URI("/user"));
    ResponseEntity<User> response = testRestTemplate.exchange(request, User.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(userService, Mockito.times(1)).update(Mockito.any(User.class));

    Assertions.assertEquals(user, response.getBody());
  }

  @Test
  public void testFindById() throws URISyntaxException {
    User user = new User();
    user.setId(1);
    user.setFirstName("Tester");
    user.setLastName("User");
    user.setLogin("tester");
    user.setPassword("12345");

    Mockito.when(userService.findById(user.getId())).thenReturn(user);

    RequestEntity<User> request = new RequestEntity<>(user, HttpMethod.GET, new URI("/user/get-by-id/" + user.getId().toString()));
    ResponseEntity<User> response = testRestTemplate.exchange(request, User.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(userService, Mockito.times(1)).findById(user.getId());

    Assertions.assertEquals(user, response.getBody());
  }

  @Test
  public void testGetAll() throws URISyntaxException {
    List<User> users = new ArrayList<>();
    Mockito.when(userService.findAll()).thenReturn(users);

    ParameterizedTypeReference<List<Item>> typeRef = new ParameterizedTypeReference<>() {};

    RequestEntity<User> request = new RequestEntity<>(HttpMethod.GET, new URI("/user/get-all"));
    ResponseEntity<List<Item>> response = testRestTemplate.exchange(request, typeRef);

    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Mockito.verify(userService, Mockito.times(1)).findAll();

    Assertions.assertEquals(response.getBody().getClass().getSimpleName(), "ArrayList");
  }

  @Test
  public void testDelete() throws URISyntaxException {
    doNothing().when(userService).delete(1);

    RequestEntity<User> request = new RequestEntity<>(HttpMethod.DELETE, new URI("/user/1"));
    ResponseEntity<Void> response = testRestTemplate.exchange(request, Void.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    Mockito.verify(userService, Mockito.times(1)).delete(1);
  }
}