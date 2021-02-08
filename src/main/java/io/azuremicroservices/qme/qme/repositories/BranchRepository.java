package io.azuremicroservices.qme.qme.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.azuremicroservices.qme.qme.models.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

	List<Branch> findAllByVendor_Id(Long vendorId);

	List<Branch> findAllByVendor_IdAndName(Long vendorId, String branchName);

}
