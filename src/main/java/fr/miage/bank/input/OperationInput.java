package fr.miage.bank.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationInput {

    @NotNull
    private String libelle;

    @NotNull
    @Min(0)
    private double montant;

    @NotNull
    @Min(0)
    private double taux;

    @NotNull
    private String compteCrediteurIban;

    @Size(min = 3)
    private String categ;
}
