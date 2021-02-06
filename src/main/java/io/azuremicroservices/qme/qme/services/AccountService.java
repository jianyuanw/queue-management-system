package io.azuremicroservices.qme.qme.services;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

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
    
    @Transactional
    public void changePerspective(MyUserDetails currentDetails, Role perspective) {    	
    	currentDetails.setPerspective(perspective);
//    	User user = this.findUserByUsername(currentDetails.getUsername());
//    	if (user.getRolePerspectives().containsKey(perspective.getDisplayValue())) {
//    		user.setPerspective(perspective);
//    	}
    	// userRepo.save(user);    	    	
    }
}
