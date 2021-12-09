package fr.miage.bank.service;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CarteRepository;
import fr.miage.bank.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository aRepository;
    private final CarteRepository cRepository;
    private final OperationRepository oRepository;

    public Iterable<Account> findAll(){
        return aRepository.findAll();
    }

    public Optional<Account> findById(String id){
        return aRepository.findById(id);
    }

    public boolean existById(String id ){
        return aRepository.existsById(id);
    }

    public Account updateAccount(Account account){
        return aRepository.save(account);
    }

    public Account createAccount(Account account){
        return aRepository.save(account);
    }

    public Iterable<Carte> findAllCartes(String id){
        return cRepository.findAllByAccount_Iban(id);
    }

    public Iterable<Operation> findAllOperations(String id){
        return oRepository.findAllByCompteCrediteur_Iban(id);
    }

    public Optional<Operation> findOperationById(String id){
        return oRepository.findById(id);
    }
}
