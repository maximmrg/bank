package fr.miage.bank.repository;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<Operation, String> {
    public Iterable<Operation> findAllByCompteDebiteur_User_IdAndCompteDebiteur_Iban(String userId, String iban);

    public Iterable<Operation> findAllByCompteDebiteurAndCateg(Account account_creditor, String categ);

    public Optional<Operation> findByIdAndCompteDebiteur_Iban(String operationId, String accountId);
}
