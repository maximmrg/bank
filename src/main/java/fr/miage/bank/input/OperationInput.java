package fr.miage.bank.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationInput {

    @NotNull
    private String libelle;

    @NotNull
    @Min(0)
    private double montant;

    @Min(0)
    private double taux;

    @NotNull
    private String compteDebiteurId;

    private String categ;

    private String pays;
}
