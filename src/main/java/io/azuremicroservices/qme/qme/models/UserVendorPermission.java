package io.azuremicroservices.qme.qme.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
public class UserVendorPermission {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@ManyToOne
	@Exclude
    private Vendor vendor;
    
	@ManyToOne
	@Exclude
    private User user;       
    
    public UserVendorPermission(User user, Vendor vendor) {
    	this.vendor = vendor;
    	this.user = user;
    }
}
