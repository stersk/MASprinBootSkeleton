package com.mainacad.controller.rest;

import com.mainacad.entity.User;
import com.mainacad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Profile("rest")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.save(user);

        if (savedUser != null) {
            return new ResponseEntity<User>(savedUser, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User updatedUser = userService.update(user);

        if (updatedUser != null) {
            return new ResponseEntity(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path="/get-by-id/{id}")
    public ResponseEntity<User> getOne(@PathVariable Integer id) {
        User userFromDB = userService.findById(id);
        if (userFromDB != null){
            return new ResponseEntity(userFromDB, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path="/get-all")
    public ResponseEntity<List> getAll() {
        List<User> users = userService.findAll();
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @DeleteMapping(path="/{id}")
    public ResponseEntity delete(@PathVariable Integer id) {
        userService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
