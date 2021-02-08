package io.azuremicroservices.qme.qme.services;

import java.util.Optional;

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
	
	
}