package io.azuremicroservices.qme.qme.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
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

    @OneToMany(mappedBy = "user")
    private List<UserBranchPermission> userBranchPermissions;

    @OneToMany(mappedBy = "user")
    private List<UserQueuePermission> userQueuePermissions;

    enum Role {
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


