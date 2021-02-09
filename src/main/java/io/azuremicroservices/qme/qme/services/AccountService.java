package io.azuremicroservices.qme.qme.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.models.UserBranchPermission;
import io.azuremicroservices.qme.qme.models.UserQueuePermission;
import io.azuremicroservices.qme.qme.models.UserVendorPermission;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import io.azuremicroservices.qme.qme.repositories.UserBranchPermissionRepository;
import io.azuremicroservices.qme.qme.repositories.UserQueuePermissionRepository;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import io.azuremicroservices.qme.qme.repositories.UserVendorPermissionRepository;
import io.azuremicroservices.qme.qme.repositories.VendorRepository;

@Service
public class AccountService {

    private final UserRepository userRepo;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;
    
    private final VendorRepository vendorRepo;
    private final BranchRepository branchRepo;
    private final QueueRepository queueRepo;
    
    private final UserVendorPermissionRepository uvpRepo;
    private final UserBranchPermissionRepository ubpRepo;
    private final UserQueuePermissionRepository uqpRepo;

    public AccountService(UserRepository userRepo, PermissionService permissionService, 
    		PasswordEncoder passwordEncoder, UserVendorPermissionRepository uvpRepo, VendorRepository vendorRepo,
    		BranchRepository branchRepo, UserBranchPermissionRepository ubpRepo, QueueRepository queueRepo,
    		UserQueuePermissionRepository uqpRepo) {
        this.userRepo = userRepo;
        this.permissionService = permissionService;
        this.passwordEncoder = passwordEncoder;
        
        this.queueRepo = queueRepo;
        this.vendorRepo = vendorRepo;
        this.branchRepo = branchRepo;
        
        this.uvpRepo = uvpRepo;
        this.ubpRepo = ubpRepo;
        this.uqpRepo = uqpRepo;
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
    	user.setPerspective(role);
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	userRepo.save(user);
    }
    
    @Transactional
    public void createUser(User user, Vendor vendor) {  	
    	user.setRole(Role.VENDOR_ADMIN);
    	user.setPerspective(Role.VENDOR_ADMIN);
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	user.setId(null);
    	userRepo.saveAndFlush(user);
    	Vendor dbVendor = vendorRepo.findById(vendor.getId()).get();
    	user.getUserVendorPermissions().add(new UserVendorPermission(user, dbVendor));
    }

    @Transactional
    public void createUser(User user, Branch branch) {
    	user.setRole(Role.BRANCH_ADMIN);
    	user.setPerspective(Role.BRANCH_ADMIN);
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	user.setId(null);
    	userRepo.saveAndFlush(user);
    	Branch dbBranch = branchRepo.findById(branch.getId()).get();
    	user.getUserBranchPermissions().add(new UserBranchPermission(user, dbBranch));	
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

	public BindingResult verifyUser(User user, BindingResult bindingResult) {	
		if (user.getId() == null) {
			if (this.emailExists(user.getEmail())) {
				bindingResult.rejectValue("email", "error.email", "Email already exists");
			}
			if (this.usernameExists(user.getUsername())) {
				bindingResult.rejectValue("username", "error.username", "Username already exists");
			}			
		} else {
			if (userRepo.findByEmailAndIdNot(user.getEmail(), user.getId()) != null) {
				bindingResult.rejectValue("email", "error.email", "Email already exists");
			}
			if (userRepo.findByUsernameAndIdNot(user.getUsername(), user.getId()) != null) {
				bindingResult.rejectValue("username", "error.username", "Username already exists");
			}
		}
		
		return bindingResult;		
	}

	public List<User> findAllUsersByRoleAndVendor(Role branchAdmin, Vendor vendor) {
		List<Long> branches = branchRepo.findAllByVendor_Id(vendor.getId()).stream()
				.map(Branch::getId)
				.collect(Collectors.toList());
		
		return ubpRepo.findAllByBranchIdIn(branches).stream()
				.map(UserBranchPermission::getUser).distinct()
				.collect(Collectors.toList());
	}

	public List<User> findAllUsersByRoleAndBranchIn(Role branchOperator, List<Branch> branches) {
		List<Long> queues = queueRepo.findAllByBranch_IdIn(branches.stream().map(Branch::getId).collect(Collectors.toList())).stream()
				.map(Queue::getId)
				.collect(Collectors.toList());

		return uqpRepo.findAllByQueueIdIn(queues).stream()
				.map(UserQueuePermission::getUser).distinct()
				.collect(Collectors.toList());
	}
}
