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

    private static final long serialVersionUID = 454854941848L;

    @Id
    private String iban;

    private String pays;

    private String noPasseport;
    private String numTel;
    private String secret;

    private double solde;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}
