package fr.miage.bank.service;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository oRepository;
    private final AccountRepository aRepository;

    public Iterable<Operation> findAllOperationsByUserIdAndAccountId(String userId, String accountId){
        return oRepository.findAllByCompteCrediteur_User_IdAndCompteCrediteur_Iban(userId, accountId);
    }

    public Iterable<Operation> findAllOperationsByAccountAndCateg(String userId, String accountIban, String categ){
        Optional<Account> creditorAccount = aRepository.findByUser_IdAndIban(userId, accountIban);
        if(creditorAccount.isPresent())
            return oRepository.findAllByCompteCrediteurAndCateg(creditorAccount.get(), categ);
        else
            return new ArrayList<>();
    }

    public Optional<Operation> findByIdAndCompteOwnerId(String operationId, String accountId){
        return oRepository.findByIdAndCompteCrediteur_Iban(operationId, accountId);
    }

    public Operation createOperation(Operation operation){
        return oRepository.save(operation);
    }
}
