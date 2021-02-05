package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchOperatorNotification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchOperatorNotificationRepository extends JpaRepository<BranchOperatorNotification, Long> {

}
