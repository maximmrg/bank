package fr.miage.bank.service;

import fr.miage.bank.entity.Carte;
import fr.miage.bank.repository.CarteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarteServices {

    private final CarteRepository cRepository;

    public Iterable<Carte> findAllCartes(String id){
        return cRepository.findAllByAccount_Id(id);
    }

}
