package io.azuremicroservices.qme.qme.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class Branch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ManyToOne
	private Vendor vendor;

	@NotEmpty(message = "Branch name must not be empty")
	@Pattern(regexp = "[A-Za-z0-9 ]+", message = "Branch name must only contain alphanumeric characters and spaces")
	private String name;

	@NotEmpty(message = "Branch address must not be empty")
	@Pattern(regexp = "[A-Za-z0-9.# -]+", message = "Branch address must only contain alphanumeric characters, periods, hashtags, dashes and spaces")
	private String address;

	@NotEmpty(message = "Branch description must not be empty")
	@Pattern(regexp = "[A-Za-z0-9 ]+", message = "Branch description must only contain alphanumeric characters and spaces")
	private String description;

	@NotNull(message = "Category must not be null")
	private BranchCategory category;

	@OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
	@Exclude
	private List<Queue> queues;

	@OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
	@Exclude
	private List<UserBranchPermission> userBranchPermissions;
	
	private String branchImage;
	
	@Transient
	public String getBranchImagePath() {
		if (branchImage == null || id == null) return null;
		
		return "/branch-images/" + id + "/" + branchImage;
	} 

}
