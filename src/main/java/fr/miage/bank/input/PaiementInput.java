package fr.miage.bank.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaiementInput {

    @NotNull
    @Min(0)
    private double montant;

    @NotNull
    private String pays;

    private String ibanCrediteur;
}
