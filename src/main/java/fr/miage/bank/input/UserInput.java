package fr.miage.bank.input;

import lombok.*;

import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {

    @Size(min = 3)
    private String nom;

    @Size(min = 2)
    private String prenom;

    private String email;

    @Size(min = 4)
    private String password;
}
