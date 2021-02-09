package io.azuremicroservices.qme.qme.models;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
@Table
public class Operator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vendor vendor;

    @NotEmpty(message = "Operator username must not be empty")
    @Pattern(regexp = "[A-Za-z0-9' ]+", message = "Operator username must only contain alphanumeric characters, spaces and single quotes")
    private String username;

    @NotEmpty(message = "Operator staffname must not be empty")
    @Pattern(regexp = "[A-Za-z0-9' ]+", message = "Operator staffname must only contain alphanumeric characters, spaces and single quotes")
    private String staffname;

    @NotEmpty(message = "Operator Authorised must not be empty")
    private String authorised;

}
