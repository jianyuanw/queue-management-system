package io.azuremicroservices.qme.qme.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import io.azuremicroservices.qme.qme.repositories.VendorRepository;

@Service
public class AccountService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    
    private final VendorRepository vendorRepo;
    private final BranchRepository branchRepo;
    private final QueueRepository queueRepo;
    
    private final UserBranchPermissionRepository ubpRepo;
    private final UserQueuePermissionRepository uqpRepo;
    
    private final SessionRegistry sessionRegistry;

    public AccountService(UserRepository userRepo, PasswordEncoder passwordEncoder, VendorRepository vendorRepo,
    		BranchRepository branchRepo, UserBranchPermissionRepository ubpRepo, QueueRepository queueRepo,
    		UserQueuePermissionRepository uqpRepo, SessionRegistry sessionRegistry) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        
        this.queueRepo = queueRepo;
        this.vendorRepo = vendorRepo;
        this.branchRepo = branchRepo;
        
        this.ubpRepo = ubpRepo;
        this.uqpRepo = uqpRepo;
        
        this.sessionRegistry = sessionRegistry;
    }

    public boolean usernameExists(String username) {
    	return userRepo.findByUsername(username) != null;
    }

    public boolean emailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public List<User> findAllUsersByRole(Role role) {
    	return userRepo.findAllByRole(role);
    }
    
    @Transactional(readOnly = true)
    public User findUserByRole(Role role) {
    	return userRepo.findByRole(role);
    }
    
    @Transactional
    public void createUser(User user, Role role) {
    	user.setRole(role);
    	user.setPerspective(role);
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	user.setBlocked(false);
    	userRepo.save(user);
    }
    
    @Transactional
    public void createUser(User user, Vendor vendor) {  	
    	user.setRole(Role.VENDOR_ADMIN);
    	user.setPerspective(Role.VENDOR_ADMIN);
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	user.setId(null);
    	user.setBlocked(false);
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
    	user.setBlocked(false);
    	userRepo.saveAndFlush(user);
    	Branch dbBranch = branchRepo.findById(branch.getId()).get();
    	user.getUserBranchPermissions().add(new UserBranchPermission(user, dbBranch));	
    }
    
    @Transactional
    public void createUser(User user, List<String> queues) {
    	user.setRole(Role.BRANCH_OPERATOR);
    	user.setPerspective(Role.BRANCH_OPERATOR);
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	user.setId(null);
    	user.setBlocked(false);
    	userRepo.saveAndFlush(user);
    	for (String q : queues) {
    		var queue = queueRepo.findById(Long.parseLong(q));
    		
    		if (queue.isPresent()) {
    			user.getUserQueuePermissions().add(new UserQueuePermission(user, queue.get()));
    		}
    	}
    }    

    @Transactional
    public void changePerspective(MyUserDetails currentDetails, Role perspective) {
        currentDetails.setPerspective(perspective);
    }

	public Optional<User> findUserById(Long userId) {
		return userRepo.findById(userId);
	}
	
	public void updateUser(User user) {
		User dbUser = userRepo.findById(user.getId()).get();
		
		if (user.getPassword() == "") {
			user.setPassword(dbUser.getPassword());
		} else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		
		userRepo.save(user);
		this.invalidateSessions(user.getId());
	}
	
	public void deleteUser(User user) {
		userRepo.delete(user);
		this.invalidateSessions(user.getId());
	}

	public BindingResult verifyUser(User user, BindingResult bindingResult) {	
		if (user.getId() == null) {
			if (this.emailExists(user.getEmail())) {
				bindingResult.rejectValue("email", "error.email", "Email already exists");
			}
			if (this.usernameExists(user.getUsername())) {
				bindingResult.rejectValue("username", "error.username", "Username already exists");
			}
			if (user.getPassword() == "") {
				bindingResult.rejectValue("password", "error.password", "Password cannot be empty");
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

	@Transactional(readOnly = true)
	public List<User> findAllUsersByRoleAndVendor(Role role, Vendor vendor) {
		List<Long> branches = branchRepo.findAllByVendor_Id(vendor.getId()).stream()
				.map(Branch::getId)
				.collect(Collectors.toList());
		
		return ubpRepo.findAllByBranchIdIn(branches).stream()
				.map(UserBranchPermission::getUser).distinct()
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<User> findAllUsersByRoleAndBranchIn(Role role, List<Branch> branches) {
		List<Long> queues = queueRepo.findAllByBranch_IdIn(branches.stream().map(Branch::getId).collect(Collectors.toList())).stream()
				.map(Queue::getId)
				.collect(Collectors.toList());

		return uqpRepo.findAllByQueueIdIn(queues).stream()
				.map(UserQueuePermission::getUser).distinct()
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<User> findAllUsers() {
		return userRepo.findAll();
	}

	public void unblockUser(Long userId) {
		User user = userRepo.findById(userId).get();
		user.setBlocked(false);
		userRepo.save(user);
		this.invalidateSessions(userId);
	}
	
	public void blockUser(Long userId) {
		User user = userRepo.findById(userId).get();		
		user.setBlocked(true);
		userRepo.save(user);
		this.invalidateSessions(userId);
	}
	
	public void invalidateSessions(Long userId) {
		List<Object> principals = sessionRegistry.getAllPrincipals();
		for (Object principal : principals) {
			if (principal instanceof MyUserDetails) {
				MyUserDetails loggedInUser = (MyUserDetails) principal;
				if (userId.equals(loggedInUser.getId())) {
					List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false );
					for (SessionInformation sessionInfo : sessionsInfo) {
						sessionInfo.expireNow();
					}
				}
			}
		}
	}	
}
