package io.azuremicroservices.qme.qme.repos;

import io.azuremicroservices.qme.qme.models.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {

}
