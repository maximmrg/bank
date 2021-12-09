package fr.miage.bank.validator;

import fr.miage.bank.input.CarteInput;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class CarteValidator {

    private Validator validator;

    public CarteValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(CarteInput carte){
        Set<ConstraintViolation<CarteInput>> violations = validator.validate(carte);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }
}
