package fr.miage.bank.input;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {

    @NotNull
    @Size(min = 3)
    private String nom;

    @NotNull
    @Size(min = 2)
    private String prenom;

    @NotNull
    private Date birthDate;

    @Size(min = 5)
    private String noPasseport;

    @Size(min = 10, max = 10)
    private String numTel;

    @NotNull
    private String email;

    @NotNull
    @Size(min = 4)
    private String password;
}
