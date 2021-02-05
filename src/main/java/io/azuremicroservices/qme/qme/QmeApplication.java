package io.azuremicroservices.qme.qme;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class QmeApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder encoder;

    public static void main(String[] args) {
        SpringApplication.run(QmeApplication.class, args);
    }

    // Temporarily using below method to encode the passwords for the five roles for testing
    @Override
    public void run(String... args) throws Exception {
        System.out.println("*** Executing CommandLineRunner ***");
        String[] usernames = { "client", "appadmin", "vendoradmin", "branchadmin", "branchoperator" };
        for (String username : usernames) {
            User user = userRepo.findByUsername(username);
            user.setPassword(encoder.encode(user.getPassword()));
            userRepo.save(user);
        }
    }
}
