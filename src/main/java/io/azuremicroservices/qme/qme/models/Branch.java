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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
@Table
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vendor vendor;

    private String name;

    private String address;

    private String description;

    private BranchCategory category;
    
    @OneToMany(mappedBy="branch", cascade=CascadeType.ALL)
    @Exclude
    private List<Queue> queues;
    
    @ManyToMany(mappedBy = "userQueuePermissions")
    @Exclude
    private List<User> userQueuePermissions;


}
