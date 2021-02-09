package io.azuremicroservices.qme.qme.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.VendorRepository;

@Service
public class VendorService {

	private final VendorRepository vendorRepo;
	
	@Autowired
	public VendorService(VendorRepository vendorRepo) {
		this.vendorRepo = vendorRepo;
	}

	public Optional<Vendor> findVendorById(Long vendorId) {
		return vendorRepo.findById(vendorId);
	}
	
	public List<Vendor> findAllVendors() {
		return vendorRepo.findAll();
	}
	
	public boolean companyUidExists(String companyUid) {
		return vendorRepo.findByCompanyUid(companyUid) != null;
	}

	public void createVendor(Vendor vendor) {
		vendorRepo.save(vendor);		
	}

	public void updateVendor(Vendor vendor) {
		vendorRepo.save(vendor);
	}

	public void deleteVendor(Vendor vendor) {
		vendorRepo.delete(vendor);
	}
	
}
