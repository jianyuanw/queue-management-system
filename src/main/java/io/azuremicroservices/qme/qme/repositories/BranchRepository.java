package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.Branch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {

	List<Branch> findAllByVendor_Id(Long vendorId);

}
