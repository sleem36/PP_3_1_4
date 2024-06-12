package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.RoleRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;
import org.springframework.web.bind.annotation.PatchMapping;



import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
//@RequestMapping("/api")
public class UserController {

    private UserServiceImpl userService;
    private RoleRepository roleRepository;

    @Autowired
    public UserController(UserServiceImpl userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

//    @GetMapping("/users")
//    public List<User> getAllUsers() {
//        return userService.getAll();
//    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAll();
        if(users != null && !users.isEmpty()) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/user")
    public User getCurrentUser(Principal principal) {
        return userService.findByName(principal.getName());
    }

    @PostMapping("/users")
    public User saveUser(@RequestBody User user) {
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
        return userService.getUser(id);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        List<Long> roleIds = user.getRoleIds();
        List<Integer> roleIntIds = roleIds.stream().map(Long::intValue).collect(Collectors.toList());
        List<Role> roles = new ArrayList<>(roleRepository.findAllById(roleIntIds));
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        return userService.saveUserWithRoles(user, new ArrayList<>(roles));
    }



}

