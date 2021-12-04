package fr.miage.bank.repository;

import fr.miage.bank.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, String> {
    public Iterable<Operation> findAllByCompteOwner_Id(String id);

    public Optional<Operation> findByIdAndAndCompteOwner_Id(String operationId, String accountId);
}
