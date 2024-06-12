package ru.kata.spring.boot_security.demo.configs;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;


@Component
public class DataInitializer implements ApplicationRunner {
// добавляем user и admin чтоб были при запуске приложения
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public DataInitializer(UserServiceImpl userService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void initializeData() {
        Role userRole = new Role("ROLE_USER");
        Role adminRole = new Role("ROLE_ADMIN");

        userRole = roleRepository.save(userRole);
        adminRole = roleRepository.save(adminRole);

        User user = new User("user", passwordEncoder.encode("user"));
        User admin = new User("admin", passwordEncoder.encode("admin"));


        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        user.setRoles(userRoles);

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        admin.setRoles(adminRoles);


        userService.saveUserInit(user, "user@test.com","user_test", 22, userRoles);
        userService.saveUserInit(admin, "admin@test.com","admin_test", 36, adminRoles);
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        this.initializeData();
    }
}