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
public class CarteInput {

    @NotNull
    @Size(min = 4, max = 4)
    private String code;

    @NotNull
    @Size(min = 3, max = 3)
    private String crypto;

    @NotNull
    private boolean bloque;

    @NotNull
    private boolean localisation;

    @NotNull
    @Min(0)
    private double plafond;

    @NotNull
    private boolean sansContact;

    @NotNull
    private boolean virtual;
}
