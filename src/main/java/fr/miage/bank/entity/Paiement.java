package fr.miage.bank.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Paiement {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne
    private Carte carte;

    private Timestamp date;
    private double montant;
    private double taux;

    private String pays;

    @ManyToOne
    private Account compteCrediteur;

}
