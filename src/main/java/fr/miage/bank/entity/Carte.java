package fr.miage.bank.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Carte {

    private static final long serialVersionUID = 765432234567L;

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String numero;
    private String code;
    private String crypto;
    private boolean bloque;
    private boolean localisation;
    private double plafond;
    private boolean sansContact;
    private boolean virtual;

    private Date dateExpiration;

    @ManyToOne
    @JoinColumn(name = "account_iban")
    private Account account;

    public Carte(String id, String numero, String code, String crypto, boolean bloque, boolean localisation, double plafond, boolean sansContact, boolean virtual, Account account) {
        this.id = id;
        this.numero = numero;
        this.code = code;
        this.crypto = crypto;
        this.bloque = bloque;
        this.localisation = localisation;
        this.plafond = plafond;
        this.sansContact = sansContact;
        this.virtual = virtual;
        this.account = account;
    }
}
