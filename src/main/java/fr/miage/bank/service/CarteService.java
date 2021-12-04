package fr.miage.bank.service;

import fr.miage.bank.entity.Carte;
import fr.miage.bank.repository.CarteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarteService {

    private final CarteRepository cRepository;

    public Iterable<Carte> findAllCartesByAccountId(String accountId){
        return cRepository.findAllByAccount_Id(accountId);
    }

    public boolean existById(String id){
        return cRepository.existsById(id);
    }

    public Optional<Carte> findByIdAndAccountId(String carteId, String accountId){
        return cRepository.findByIdAndAccount_Id(carteId, accountId);
    }

    public Carte createCarte(Carte carte){
        return cRepository.save(carte);
    }

    public Carte updateCarte(Carte carte){
        return cRepository.save(carte);
    }
}
