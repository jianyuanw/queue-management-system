package io.azuremicroservices.qme.qme.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
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
public class Vendor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@NotEmpty(message = "Company Uid must not be empty")
	private String companyUid;
	
    @NotEmpty(message = "Vendor name must not be empty")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "Vendor name must only contain alphanumeric characters and spaces")
	private String name;

	@NotEmpty(message = "Vendor name must not be empty")
	@Pattern(regexp = "[A-Za-z0-9 ]+", message = "Vendor name must only contain alphanumeric characters, spaces and single quotes")      
	private String description;

	@OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
	@Exclude
	private List<Branch> branches = new ArrayList<>();

	@OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
	@Exclude
	private List<UserVendorPermission> userVendorPermissions = new ArrayList<>();
	
	private String vendorImage;
	
	@Transient
	public String getVendorImagePath() {
		if (vendorImage == null || id == null) return null;
		
		return "/vendor-images/" + id + "/" + vendorImage;
	} 

}
