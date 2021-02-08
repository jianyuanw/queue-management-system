package io.azuremicroservices.qme.qme.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

@Service
public class AccountService {

    private final UserRepository userRepo;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserRepository userRepo, PermissionService permissionService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.permissionService = permissionService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
    	return userRepo.findByUsername(username) != null;
    }

    public boolean emailExists(String email) {
        return userRepo.findByEmail(email) != null;
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

    @Transactional
    public void createUser(User user, Role role) {
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }
    
    public void createUser(User user, Vendor vendor) {
    	user.getUserVendorPermissions().add(vendor);
    	this.createUser(user, Role.VENDOR_ADMIN);
    }
    
    public void createUser(User user, Branch branch) {
    	user.getUserBranchPermissions().add(branch);
    	this.createUser(user, Role.BRANCH_ADMIN);
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

	public Optional<User> findUserById(Long userId) {
		return userRepo.findById(userId);
	}
	
	public void updateUser(User user) {
		userRepo.save(user);
	}
	
	public void deleteUser(User user) {
		userRepo.delete(user);
	}
}
