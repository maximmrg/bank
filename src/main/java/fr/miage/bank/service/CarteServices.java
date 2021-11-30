package fr.miage.bank.service;

import fr.miage.bank.entity.Carte;
import fr.miage.bank.repository.CarteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarteServices {

    private final CarteRepository cRepository;

    public Iterable<Carte> findAllCartesByAccountId(String accountId){
        return cRepository.findAllByAccount_Id(accountId);
    }

    public Optional<Carte> findByIdAndAccountId(String carteId, String accountId){
        return cRepository.findByIdAndAccount_Id(carteId, accountId);
    }
}
