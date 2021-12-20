package fr.miage.bank.validator;

import fr.miage.bank.input.OperationInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class OperationValidator {

    private Validator validator;

    public OperationValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(OperationInput operation){
        Set<ConstraintViolation<OperationInput>> violations = validator.validate(operation);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }
}
