package io.azuremicroservices.qme.qme.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
@Table
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyUid;

    private String name;

    private String description;
    
    @OneToMany (mappedBy="vendor", cascade=CascadeType.ALL)
    @Exclude
    private List<Branch> branches;
    
    @ManyToMany(mappedBy = "userVendorPermissions")
    private List<User> userVendorPermissions;
    

}
