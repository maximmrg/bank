package fr.miage.bank.entity;

import lombok.*;
import org.springframework.hateoas.PagedModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account implements Serializable {

    private static final long serialVersionUID = 765432234567L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    private String nom;
    private String prenom;
    private Date birthDate;
    private String pays;

    private String noPasseport;
    private String numTel;
    private String secret;
    private String iban;
}
