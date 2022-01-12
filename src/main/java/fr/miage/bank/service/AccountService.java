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

    public Iterable<Account> findAllByUserId(String userId){
        return aRepository.findAllByUser_Id(userId);
    }

    public Optional<Account> findById(String id){
        return aRepository.findById(id);
    }

    public Optional<Account> findByUserIdAndIban(String userId, String iban){
        return aRepository.findByUser_IdAndIban(userId, iban);
    }

    public Optional<Account> findByIban(String iban){
        return aRepository.findById(iban);
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

    public void debiterAccount(Account account, double montant) {
        account.debiterCompte(montant);
        aRepository.save(account);
    }

    public void crediterAccount(Account account, double montant, double taux){
        account.crediterCompte(montant, taux);
        aRepository.save(account);
    }
}
