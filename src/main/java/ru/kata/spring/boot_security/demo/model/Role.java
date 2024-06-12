package ru.kata.spring.boot_security.demo.model;

import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    public void addUser(User user) {
        if (users == null) {
            users = new ArrayList<>();
        }
        users.add(user);
    }

    public void removeUser(User user) {
        if (users != null) {
            users.remove(user);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public Role() {
        // конструктор по умолчанию
    }

    public Role(String name) {
        this.name = name;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

}