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

    private int code;
    private int crypto;
    private boolean bloque;
    private boolean localisation;
    private int plafond;
    private boolean sansContact;
    private boolean virtual;

    @ManyToOne
    private Account account;
}
