package com.construction.management.config;

import com.construction.management.entity.Role;
import com.construction.management.entity.User;
import com.construction.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.owner.email:owner@construction-mgmt.local}")
    private String ownerEmail;

    @Value("${app.owner.password:Owner@123456}")
    private String ownerPassword;

    @Value("${app.owner.name:System Owner}")
    private String ownerName;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmail(ownerEmail)) {
            log.info("Seeding initial Owner user with email: {}", ownerEmail);
            User owner = new User(
                    ownerName,
                    ownerEmail,
                    passwordEncoder.encode(ownerPassword),
                    null,
                    Role.OWNER
            );
            userRepository.save(owner);
            log.info("Owner user successfully created with role: {}", Role.OWNER);
        } else {
            log.debug("Owner user already exists with email: {}", ownerEmail);
        }
    }
}
