package fr.miage.bank.entity;

import com.sun.istack.NotNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {

    @NotNull
    private String nom;

    @Size(min = 2)
    private String prenom;

    @NotNull
    private Date birthDate;

    @NotNull
    private String pays;

    @Size(min = 5)
    private String noPasseport;

    @Size(min = 10, max = 10)
    private String numTel;

    @Size(min = 5, max = 10)
    private String secret;

    @Size(min = 3)
    private String IBAN;
}
