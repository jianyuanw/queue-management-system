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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
@Table
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotEmpty(message = "Username must not be empty")
    @Pattern(regexp = "[A-Za-z0-9._]+", message = "Username must only contain alphanumeric characters, periods and underscores")
    private String username;

    private String password;

    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email must not be empty")    
    private String email;

    @Pattern(regexp="^[0-9]*$", message = "Handphone number must be a number")
    private String handphoneNo;

    private String firstName;

    private String lastName;

    private Role role;
    
    private Role perspective;
    
    private boolean blocked;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    @Exclude
    @EqualsAndHashCode.Exclude
    private Counter counter;
    
    @OneToMany(mappedBy = "user")
    @Exclude
    private List<SupportTicket> supportTickets;
    
    @OneToMany(mappedBy = "user")
    @Exclude
    private List<Message> messages;    
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Exclude
    private List<QueuePosition> queuePositions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Exclude
    private List<UserVendorPermission> userVendorPermissions = new ArrayList<>();    
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Exclude
    private List<UserBranchPermission> userBranchPermissions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Exclude
    private List<UserQueuePermission> userQueuePermissions = new ArrayList<>();  
    
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
        CLIENT(0),
        APP_ADMIN(4),
        VENDOR_ADMIN(3),
        BRANCH_ADMIN(2),
        BRANCH_OPERATOR(1);

        private final String displayValue;
        private final Integer authority;

        Role(Integer authority) {
            // Generalized constructor that converts capitalized enum values to TitleCase
            StringBuilder sb = new StringBuilder();

            for (String word : this.name().split("_")) {
                sb.append(word.charAt(0)).append(word.substring(1).toLowerCase()).append(" ");
            }

            this.displayValue = sb.toString().trim();
            this.authority = authority;
        }

        public String getDisplayValue() { return displayValue; }
        public Integer getAuthority() { return authority; }
    }
}


