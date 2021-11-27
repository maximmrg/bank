package fr.miage.bank.service;

import fr.miage.bank.entity.Account;
import fr.miage.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository aRepository;

    public Iterable<Account> findAll(){
        return aRepository.findAll();
    }

    public Optional<Account> findById(long id){
        return aRepository.findById(id);
    }
}
