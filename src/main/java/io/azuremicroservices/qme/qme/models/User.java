package io.azuremicroservices.qme.qme.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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

    @OneToMany(mappedBy = "user")
    private List<SupportTicket> supportTickets;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(
    		name = "user_vendor_permission",
    		joinColumns = { @JoinColumn(name = "user_id") },
    		inverseJoinColumns = { @JoinColumn(name = "vendor_id") }
    )
    private List<Vendor> userVendorPermissions = new ArrayList<Vendor>();    
    
    @OneToMany(mappedBy = "user")
    private List<UserBranchPermission> userBranchPermissions;

    @OneToMany(mappedBy = "user")
    private List<UserQueuePermission> userQueuePermissions;

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


