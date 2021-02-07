package io.azuremicroservices.qme.qme.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

@Service
public class AccountService {

    private final UserRepository userRepo;
    private final PermissionService permissionService;

    public AccountService(UserRepository userRepo, PermissionService permissionService) {
        this.userRepo = userRepo;
        this.permissionService = permissionService;
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
    
    public List<User> findAllUsersByRole(Role role) {
    	return userRepo.findAllByRole(role);
    }
    
    public User findUserByRole(Role role) {
    	return userRepo.findByRole(role);
    }

    public void createClient(User user) {
        user.setRole(Role.CLIENT);
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

    public void createVendorAdmin(User user, Vendor vendor) {
    	user.setRole(Role.VENDOR_ADMIN);
    	user.getUserVendorPermissions().add(vendor);
    	userRepo.save(user);
    }

	public User findUserById(Long userId) {
		return userRepo.findById(userId).get();
	}
	
	public void updateUser(User user) {
		userRepo.save(user);
	}
	
	public void deleteUserById(Long userId) {
		var user = userRepo.findById(userId);
		if (user.isPresent()) {
			userRepo.delete(user.get());
		}
	}

}
