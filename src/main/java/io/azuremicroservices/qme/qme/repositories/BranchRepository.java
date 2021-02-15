package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

	List<Branch> findAllByVendor_Id(Long vendorId);

	List<Branch> findAllByVendor_IdAndName(Long vendorId, String branchName);

	List<Branch> findAllByCategory(BranchCategory branchCategory);

	List<Branch> findAllByCategoryAndNameContaining(BranchCategory branchCategory, String query);

	List<Branch> findAllByNameContaining(String query);

	List<Branch> findAllByUserBranchPermissions_IdIn(List<Long> userBranchPermissionIds);

}
