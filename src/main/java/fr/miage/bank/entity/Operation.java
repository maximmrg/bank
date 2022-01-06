package fr.miage.bank.entity;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Operation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    private Timestamp date;
    private String libelle;
    private double montant;
    private double taux;

    @ManyToOne
    @JoinColumn(name = "debitor_account_iban")
    private Account compteDebiteur;

    @ManyToOne
    @JoinColumn(name = "creditor_account_iban")
    private Account compteCrediteur;

    //private String nomCrediteur;

    private String categ;
}
