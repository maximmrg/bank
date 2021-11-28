package fr.miage.bank.entity;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    private Date date;
    private Date heure;
    private String libelle;
    private double montant;
    private double taux;

    private String IBAN_crediteur;
    private String nomCrediteur;

    @JoinColumn
    @OneToOne
    private Account compteOwner;


    private String categ;
    private String pays;

}
