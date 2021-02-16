package io.azuremicroservices.qme.qme.configuration.mvc;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path branchUploadDir = Paths.get("./branch-images");
		String branchUploadPath = branchUploadDir.toFile().getAbsolutePath();
		
		registry.addResourceHandler("/branch-images/**").addResourceLocations("file:/" + branchUploadPath + "/");
	}

}
