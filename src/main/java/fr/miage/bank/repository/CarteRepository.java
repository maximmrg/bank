package fr.miage.bank.repository;

import fr.miage.bank.entity.Carte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteRepository extends JpaRepository<Carte, Long> {
    public Iterable<Carte> findAllByAccount_Id(long id);
}
