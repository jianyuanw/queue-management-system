package io.azuremicroservices.qme.qme.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table
public class UserVendorPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;	
		
	@ManyToOne
	private User user;
		
	@ManyToOne
	private Vendor vendor;
}
