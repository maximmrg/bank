package fr.miage.bank.repository;

import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, String> {

    List<Paiement> getAllByCarte_Id(String carteId);
    List<Paiement> getAllByCarte(Carte carte);
    Optional<Paiement> findByIdAndCarte(String id, Carte carte);
}
