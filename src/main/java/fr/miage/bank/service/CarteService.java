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
        return cRepository.findAllByAccount_Iban(accountId);
    }

    public boolean existById(String id){
        return cRepository.existsById(id);
    }

    public Optional<Carte> findByIdAndAccountId(String carteId, String accountId){
        return cRepository.findByIdAndAccount_Iban(carteId, accountId);
    }

    public Carte createCarte(Carte carte){
        return cRepository.save(carte);
    }

    public Carte updateCarte(Carte carte){
        return cRepository.save(carte);
    }

    public void deleteCarte(Carte carte){
        cRepository.delete(carte);
    }

    public void deleteVirtualCarte(Carte carte){
        carte.setDeleted(true);
        cRepository.save(carte);
    }

    public Carte blockCarte(Carte carte){
        carte.setBloque(true);
        return cRepository.save(carte);
    }

    public Carte activeLocalisation(Carte carte){
        carte.setLocalisation(true);
        return cRepository.save(carte);
    }

    public Carte setPlafond(Carte carte, int plafond){
        carte.setPlafond(plafond);
        return cRepository.save(carte);
    }

    public Carte setSansContact(Carte carte){
        carte.setSansContact(true);
        return cRepository.save(carte);
    }

    public Carte unsetSansContact(Carte carte){
        carte.setSansContact(false);
        return cRepository.save(carte);
    }
}
