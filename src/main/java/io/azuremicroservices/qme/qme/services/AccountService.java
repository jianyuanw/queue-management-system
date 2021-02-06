package io.azuremicroservices.qme.qme.services;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

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

    public void createClient(User user) {
        user.setRole(Role.CLIENT);
        userRepo.save(user);
    }
    
    @Transactional
    public void createVendorAdmin(User user, Vendor vendor) {
    	user.setRole(Role.VENDOR_ADMIN);
    	user.getUserVendorPermissions().add(vendor);
    	userRepo.save(user);
    }
    

}
