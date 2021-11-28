package fr.miage.bank.repository;

import fr.miage.bank.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, String> {
    public Iterable<Operation> findAllByCompteOwner_Id(String id);
}
