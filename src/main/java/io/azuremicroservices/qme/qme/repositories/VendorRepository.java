package io.azuremicroservices.qme.qme.repositories;

import io.azuremicroservices.qme.qme.models.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
	Vendor findByCompanyUid(String companyUid);
}
