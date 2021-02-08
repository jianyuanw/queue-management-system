package io.azuremicroservices.qme.qme.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.azuremicroservices.qme.qme.annotations.UniqueEmail;
import io.azuremicroservices.qme.qme.repositories.UserRepository;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
 
    private UserRepository userRepo;
 
    public UniqueEmailValidator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
 
    public void initialize(UniqueEmail constraint) {
    }
 
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && userRepo.findByEmail(email) == null;
    }
 
}
