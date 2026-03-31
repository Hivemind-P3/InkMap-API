package com.cenfotec.inkmapapi.seeder;

import com.cenfotec.inkmapapi.models.User;
import com.cenfotec.inkmapapi.models.enums.Role;
import com.cenfotec.inkmapapi.repository.UserRepository;
import com.cenfotec.inkmapapi.service.PreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PreferencesService preferencesService;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@admin.com");
            admin.setName("Admin");
            admin.setPassword(passwordEncoder.encode("Admin123#"));
            admin.setRole(Role.ADMIN);
            admin.setProvider("LOCAL");

            userRepository.save(admin);

            preferencesService.setDefaultPreferences(admin);

            System.out.println("Admin user created");
        } else {
            System.out.println("Admin user already exists, skipping...");
        }
    }
}
