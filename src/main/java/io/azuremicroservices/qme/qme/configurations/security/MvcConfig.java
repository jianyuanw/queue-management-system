package io.azuremicroservices.qme.qme.configurations.security;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path branchUploadDir = Paths.get("src/main/resources/static/images/branch-images");
		String branchUploadPath = branchUploadDir.toFile().getAbsolutePath();
		
		Path vendorUploadDir = Paths.get("src/main/resources/static/images/vendor-images");
		String vendorUploadPath = vendorUploadDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/vendor-images/**").addResourceLocations("file:/" + vendorUploadPath + "/");
		registry.addResourceHandler("/branch-images/**").addResourceLocations("file:/" + branchUploadPath + "/");
	}

}
