package fr.miage.bank.service;

import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.repository.AccountRepository;
import fr.miage.bank.repository.CarteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository aRepository;
    private final CarteRepository cRepository;

    public Iterable<Account> findAll(){
        return aRepository.findAll();
    }

    public Optional<Account> findById(long id){
        return aRepository.findById(id);
    }

    public Iterable<Carte> findAllCartes(long id){
        return cRepository.findAllByAccount_Id(id);
    }
}
