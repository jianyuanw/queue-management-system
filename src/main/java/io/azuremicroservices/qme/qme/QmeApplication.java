package io.azuremicroservices.qme.qme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.azuremicroservices.qme.qme.repositories.UserRepository;

@SpringBootApplication
public class QmeApplication {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder encoder;

    public static void main(String[] args) {
        SpringApplication.run(QmeApplication.class, args);
    }

    // Temporarily using below method to encode the passwords for the five roles for testing
    // Commented out because seeding now incorporates bcrypting
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("*** Executing CommandLineRunner ***");
//        String[] usernames = { "client", "appadmin", "vendoradmin", "branchadmin", "branchoperator" };
//        for (String username : usernames) {
//            User user = userRepo.findByUsername(username);
//            user.setPassword(encoder.encode(user.getPassword()));
//            userRepo.save(user);
//        }
//    }
}
