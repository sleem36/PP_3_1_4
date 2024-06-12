package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.RoleRepository;
import ru.kata.spring.boot_security.demo.dao.UserRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User saveUserWithRoles(User user, Collection<Role> roles) {
        PasswordEncoder passwordEncoder2 = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder2.encode(user.getPassword()));
        user.getRoles().addAll(roles);
        userRepository.save(user);
        return user;
    }

    public void saveUserInit(User user, String email, String lastName, int age, Collection<Role> roles) {
        user.setEmail(email);
        user.setLastName(lastName);
        user.setAge(age);
        user.getRoles().addAll(roles);
        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUser (int id) {
        User user = null;
        Optional<User> us = userRepository.findById(id);
        if (us.isPresent()) {
            user = us.get();
        }
        return user;
    }

    public User deleteUser(int id) {
        userRepository.deleteById(id);
        return null;
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByName(name);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }


}
