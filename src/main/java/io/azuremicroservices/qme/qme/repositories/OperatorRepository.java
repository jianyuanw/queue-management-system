package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {
    List<Operator> findAllByBranch_Id(Long branchId);

    List<Operator> findAllByBranch_IdAndName(Long branchId, String branchName);
}
