package io.azuremicroservices.qme.qme.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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

	public void createVendor(MultipartFile vendorImage, Vendor vendor) throws IOException {
		String fileName = StringUtils.cleanPath(vendorImage.getOriginalFilename());
		vendor.setVendorImage(fileName);
		Vendor savedVendor = vendorRepo.save(vendor);
		String uploadDir = "src/main/resources/static/images/vendor-images/" + savedVendor.getId();
		
		if(!vendorImage.isEmpty()) {
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
		
			try (InputStream inputStream = vendorImage.getInputStream()){
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new IOException("Could not save uploaded file:" + fileName);
			}
		}
	}

	public void updateVendor(MultipartFile vendorImage, Vendor vendor) throws IOException {
		if (!vendorImage.isEmpty()) {
			String fileName = StringUtils.cleanPath(vendorImage.getOriginalFilename());
			vendor.setVendorImage(fileName);
			Vendor savedVendor = vendorRepo.save(vendor);
			String uploadDir = "src/main/resources/static/images/vendor-images/" + savedVendor.getId();

			if(vendor.getVendorImage() != null) {
				FileUtils.deleteDirectory(new File(uploadDir));
			}

			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			try (InputStream inputStream = vendorImage.getInputStream()){
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new IOException("Could not save uploaded file:" + fileName);
			}
		} else {
			vendorRepo.save(vendor);
		}
	}

	public void deleteVendor(Vendor vendor) throws IOException {
		if(!vendor.getVendorImage().isBlank()) {
			String uploadDir = "src/main/resources/static/images/vendor-images/" + vendor.getId();
			
			FileUtils.deleteDirectory(new File(uploadDir));
			}
		
		vendorRepo.delete(vendor);
	}
	
}
