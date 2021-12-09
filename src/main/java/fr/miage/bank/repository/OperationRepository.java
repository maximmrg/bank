package fr.miage.bank.repository;

import fr.miage.bank.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, String> {
    public Iterable<Operation> findAllByCompteCrediteur_Iban(String id);

    public Optional<Operation> findByIdAndCompteCrediteur_Iban(String operationId, String accountId);
}
