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

    public boolean usernameExists(String username) {
        if (userRepo.findByUsername(username) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean emailExists(String email) {
        if (userRepo.findByEmail(email) != null) {
            return true;
        } else {
            return false;
        }
    }

    public User findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public void createClient(User user) {
        user.setRole(User.Role.CLIENT);
        userRepo.save(user);
    }
}
