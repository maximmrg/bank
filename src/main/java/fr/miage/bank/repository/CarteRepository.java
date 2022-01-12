package fr.miage.bank.repository;

import fr.miage.bank.entity.Carte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface CarteRepository extends JpaRepository<Carte, String> {
    public Iterable<Carte> findAllByAccount_Iban(String id);

    public Optional<Carte> findByIdAndAccount_Iban(String carteId, String accountId);

    public Optional<Carte> findByNumeroAndCryptoAndDateExpirationAndAccount_User_Nom(String numCarte, String cryptoCarte, Date expCartee, String nomUser);
}
