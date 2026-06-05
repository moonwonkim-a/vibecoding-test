package com.example.library.common.config;

import com.example.library.admin.entity.LibraryAdmin;
import com.example.library.admin.repository.LibraryAdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MockDataInitializer implements CommandLineRunner {

    private final LibraryAdminRepository adminRepository;

    public MockDataInitializer(LibraryAdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            LibraryAdmin admin = new LibraryAdmin(
                    "admin",
                    encoder.encode("admin1234"),
                    "관리자",
                    LocalDateTime.now()
            );
            adminRepository.save(admin);
        }
    }
}
