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
    @Id
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
