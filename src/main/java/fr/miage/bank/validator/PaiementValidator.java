package fr.miage.bank.validator;

import fr.miage.bank.input.PaiementInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class PaiementValidator {

    private Validator validator;

    public PaiementValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(PaiementInput paiement){
        Set<ConstraintViolation<PaiementInput>> violations = validator.validate(paiement);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }
}
