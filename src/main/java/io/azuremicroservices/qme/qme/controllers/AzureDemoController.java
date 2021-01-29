package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping
public class AzureDemoController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/azureprototype")
    public String azurePrototype() {
        return "base";
    }

    @GetMapping("azureprototype/add")
    public String addUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email@email.com");
        userRepo.save(user);
        return "redirect:/azureprototype";
    }

    @GetMapping("azureprototype/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        User user = userRepo.findById(userId).get();
        userRepo.delete(user);
        return "redirect:/azureprototype";
    }
}
