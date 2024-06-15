package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.RoleRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserServiceImpl userService;
    private RoleRepository roleRepository;

    @Autowired
    public UserController(UserServiceImpl userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Вызван метод getAllUsers");
        List<User> users = userService.getAll();
        if(users != null && !users.isEmpty()) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users")
    public User saveUser(@RequestBody User user) {
        logger.info("Вызван метод saveUser с параметрами: " + user.toString());
        List<Long> roleIds = user.getRoleIds();
        List<Integer> roleIntIds = roleIds.stream().map(Long::intValue).collect(Collectors.toList());
        List<Role> roles = new ArrayList<>(roleRepository.findAllById(roleIntIds));
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        return userService.saveUserWithRoles(user, new ArrayList<>(roles));
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") int id) {
        logger.info("Вызван метод getUserById с ID: " + id);
        return userService.getUser(id);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        logger.info("Вызван метод deleteUser с ID: " + id);
        userService.deleteUser(id);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        logger.info("Вызван метод updateUser с параметрами: " + user.toString());
        List<Long> roleIds = user.getRoleIds();
        List<Integer> roleIntIds = roleIds.stream().map(Long::intValue).collect(Collectors.toList());
        List<Role> roles = new ArrayList<>(roleRepository.findAllById(roleIntIds));
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        return userService.saveUserWithRoles(user, new ArrayList<>(roles));
    }



}

