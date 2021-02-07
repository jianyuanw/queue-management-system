package io.azuremicroservices.qme.qme.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Username must not be empty")
    @Pattern(regexp = "[A-Za-z0-9.]+", message = "Username must only contain alphanumeric characters and period")
    private String username;

    @NotEmpty(message = "Password must not be empty")
    private String password;

    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email must not be empty")
    private String email;

    private String handphoneNo;

    private String firstName;

    private String lastName;

    private Role role;
    
    private Role perspective;

    @OneToMany(mappedBy = "user")
    private List<SupportTicket> supportTickets;
    
    @OneToMany(mappedBy = "user", cascade=CascadeType.ALL)
    private List<QueuePosition> queuePositions;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(
    		name = "user_vendor_permission",
    		joinColumns = { @JoinColumn(name = "user_id") },
    		inverseJoinColumns = { @JoinColumn(name = "vendor_id") }
    )
    private List<Vendor> userVendorPermissions = new ArrayList<Vendor>();    
    
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(
    		name = "user_branch_permission",
    		joinColumns = { @JoinColumn(name = "user_id") },
    		inverseJoinColumns = { @JoinColumn(name = "branch_id") }
    )
    private List<Branch> userBranchPermissions;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(
    		name = "user_queue_permission",
    		joinColumns = { @JoinColumn(name = "user_id") },
    		inverseJoinColumns = { @JoinColumn(name = "queue_id") }
    )
    private List<Queue> userQueuePermissions;
    
    public List<Vendor> getVendorPermissions() {    	
    	List<Vendor> vendorPermissions = this.getUserVendorPermissions();
    	// Set to ensure only one vendor permission without changing db design for future proofing
    	if (vendorPermissions.size() != 1) {
    		// Intentionally return empty list
    		vendorPermissions = new ArrayList<>();
    	} 
    	
    	return vendorPermissions;
    }     
    
    public LinkedHashMap<String, Integer> getRolePerspectives() {
    	HashMap<Role, Role[]> allowedPerspectives = new HashMap<>();
    	allowedPerspectives.put(Role.CLIENT, new Role[] {Role.CLIENT});
    	allowedPerspectives.put(Role.APP_ADMIN, new Role[] {Role.APP_ADMIN});
    	allowedPerspectives.put(Role.VENDOR_ADMIN, new Role[] {Role.VENDOR_ADMIN, Role.BRANCH_ADMIN, Role.BRANCH_OPERATOR});
    	allowedPerspectives.put(Role.BRANCH_ADMIN, new Role[] {Role.BRANCH_ADMIN, Role.BRANCH_OPERATOR});
    	allowedPerspectives.put(Role.BRANCH_OPERATOR, new Role[] {Role.BRANCH_OPERATOR});
    	
    	LinkedHashMap<String, Integer> currentPerspectives = new LinkedHashMap<>();
    	if (allowedPerspectives.get(this.role).length < 2) {
    		currentPerspectives = null;
    	} else {
    		// currentPerspectives.put(this.perspective.getDisplayValue(), this.perspective.ordinal());
    		for (Role role : allowedPerspectives.get(this.role)) {
    			if (!role.equals(this.perspective)) {
    				currentPerspectives.put(role.getDisplayValue(), role.ordinal());
    			}
    		}
    	}
    	
    	return currentPerspectives;
    }
    
    public enum Role {
        CLIENT,
        APP_ADMIN,
        VENDOR_ADMIN,
        BRANCH_ADMIN,
        BRANCH_OPERATOR;

        private final String displayValue;

        Role() {
            // Generalized constructor that converts capitalized enum values to TitleCase
            StringBuilder sb = new StringBuilder();

            for (String word : this.name().split("_")) {
                sb.append(word.charAt(0)).append(word.substring(1).toLowerCase()).append(" ");
            }

            this.displayValue = sb.toString().trim();
        }

        public String getDisplayValue() { return displayValue; }
    }
}


