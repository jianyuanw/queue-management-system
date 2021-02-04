package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final UserRepository userRepo;

    public AccountService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void createClient(User user) {
        user.setRole(User.Role.CLIENT);
        userRepo.save(user);
    }

    public boolean verifyUser(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}
