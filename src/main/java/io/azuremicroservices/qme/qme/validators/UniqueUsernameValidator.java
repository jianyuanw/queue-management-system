package io.azuremicroservices.qme.qme.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.azuremicroservices.qme.qme.annotations.UniqueUsername;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
 
    private UserRepository userRepo;
 
    public UniqueUsernameValidator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
 
    public void initialize(UniqueUsername constraint) {
    }
 
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && userRepo.findByUsername(username) == null;
    }
 
}
