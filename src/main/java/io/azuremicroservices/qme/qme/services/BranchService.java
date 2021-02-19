package io.azuremicroservices.qme.qme.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchCategory;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;

@Service
public class BranchService {
	
	private final BranchRepository branchRepo;
	
	@Autowired
	public BranchService(BranchRepository branchRepo) {
		this.branchRepo = branchRepo;
	}

	public List<Branch> findAllBranches() {
		return branchRepo.findAll();
	}

	public void createBranch(MultipartFile branchImage, Branch branch) throws IOException {
		String fileName = StringUtils.cleanPath(branchImage.getOriginalFilename());
		branch.setBranchImage(fileName);
		Branch savedBranch = branchRepo.save(branch);
		String uploadDir = "src/main/resources/static/images/branch-images/" + savedBranch.getId();
		
		
		if(!branchImage.isEmpty()) {
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
		
			try (InputStream inputStream = branchImage.getInputStream()){
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new IOException("Could not save uploaded file:" + fileName);
			}
		}
	}
	
	public void updateBranch(MultipartFile branchImage, Branch branch) throws IOException {
		if (!branchImage.isEmpty()) {
			String fileName = StringUtils.cleanPath(branchImage.getOriginalFilename());
			branch.setBranchImage(fileName);
			Branch savedBranch = branchRepo.save(branch);
			String uploadDir = "src/main/resources/static/images/branch-images/" + savedBranch.getId();

			if(branch.getBranchImage() != null) {
				FileUtils.deleteDirectory(new File(uploadDir));
			}

			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = branchImage.getInputStream()){
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new IOException("Could not save uploaded file:" + fileName);
			}
		} else {
			branchRepo.save(branch);
		}
	}	

	public Optional<Branch> findBranchById(Long branchId) {
		return branchRepo.findById(branchId);
	}

	public List<Branch> findAllBranchesByVendorId(Long vendorId) {		
		return branchRepo.findAllByVendor_Id(vendorId);
	}
	
	public boolean branchNameExistsForVendor(String branchName, Long vendorId) {
		return branchRepo.findAllByVendor_IdAndName(vendorId, branchName).size() > 0;
	}

	@Transactional
	public void deleteBranch(Branch branch) throws IOException {
		if(!branch.getBranchImage().isBlank()) {
		String uploadDir = "src/main/resources/static/images/branch-images/" + branch.getId();
		
		FileUtils.deleteDirectory(new File(uploadDir));
		}
		branchRepo.delete(branch);
	}	
	
    public List<Branch> findBranchesByQuery(String query) {
        return branchRepo.findAllByNameContaining(query);
    }

	public List<Branch> findBranchesByCategory(String category) {
		try {
			BranchCategory branchCategory = Enum.valueOf(BranchCategory.class, category.toUpperCase());
			return branchRepo.findAllByCategory(branchCategory);
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}

	}

	public List<Branch> findBranchesByQueryAndCategory(String query, String category) {
		try {
			BranchCategory branchCategory = Enum.valueOf(BranchCategory.class, category.toUpperCase());
			return branchRepo.findAllByCategoryAndNameContaining(branchCategory, query);
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}

	}

	public String parseSearchQuery(String query, String category, List<Branch> branches) {
		String messageQuery = "";		
		
		if (category == null) {
			branches.addAll(this.findBranchesByQuery(query));
			messageQuery = "Search: " + query;
		} else if (query == null || query == "") {
			branches.addAll(this.findBranchesByCategory(category));
			messageQuery = "Category: " + category;
		} else {
			branches.addAll(this.findBranchesByQueryAndCategory(query, category));
			messageQuery = "Category: " + category + ", Search: " + query;
		}		
		
		return messageQuery;
	}

	
}
