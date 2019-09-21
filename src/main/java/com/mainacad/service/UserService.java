package com.mainacad.service;

import com.mainacad.dao.UserDAO;
import com.mainacad.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Setter
@Getter
public class UserService {
    @Autowired
    private UserDAO userDAO;

    public User save(User user){
        return userDAO.save(user);
    }

    public User findById(Integer id){
        return userDAO.findById(id);
    }


    public User findByLoginAndPassword(String login, String password){
        AtomicReference<User> user = new AtomicReference<>();

        List<User> users = userDAO.findByLogin(login);
        users.forEach(u -> {
            if (u.getPassword().equals(password)) {
                user.set(u);
            }
        });

        return user.get();
    }

    public User update(User user) {
      return userDAO.update(user);
    }

    public User findByLogin(String login){
        List<User> users = userDAO.findByLogin(login);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }

    public void delete(Integer id) {
        userDAO.delete(userDAO.findById(id));
    }
}
