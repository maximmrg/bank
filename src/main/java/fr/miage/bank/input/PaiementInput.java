package fr.miage.bank.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaiementInput {

    @NotNull
    @Min(0)
    private double montant;

    @NotNull
    private String pays;

    @NotNull
    private String ibanCrediteur;

    @NotNull
    @Pattern(regexp = "([0-9]{16})")
    private String numCarte;

    @NotNull
    private Date dateExpCarte;

    @NotNull
    @Pattern(regexp = "([0-9]{3})")
    private String cryptoCarte;

    @NotNull
    @Size(min = 3)
    private String nomUser;
}
