package fr.miage.bank.validator;

import fr.miage.bank.input.UserInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class UserValidator {

    private Validator validator;

    public UserValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(UserInput user){
        Set<ConstraintViolation<UserInput>> violations = validator.validate(user);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }
}
