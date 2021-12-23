package fr.miage.bank.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Carte {

    private static final long serialVersionUID = 765432234567L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "account_iban")
    private Account account;
}
