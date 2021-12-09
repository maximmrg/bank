package fr.miage.bank.input;

import com.sun.istack.NotNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {

    @Size(min = 3)
    private String IBAN;

    @NotNull
    private String pays;

    @Size(min = 5)
    private String noPasseport;

    @Size(min = 10, max = 10)
    private String numTel;

    @Size(min = 5, max = 10)
    private String secret;

    private double solde;

    private String UserId;


}
