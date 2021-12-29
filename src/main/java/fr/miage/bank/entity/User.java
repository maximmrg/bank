package fr.miage.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 454854941848L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    private String nom;
    private String prenom;
    private Date birthDate;
    private String noPasseport;
    private String numTel;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    public User(String id, String nom, String prenom, Date birthDate, String noPasseport, String numTel, String email, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.birthDate = birthDate;
        this.noPasseport = noPasseport;
        this.numTel = numTel;
        this.email = email;
        this.password = password;
    }
}
