package fr.miage.bank.service;

import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.Paiement;
import fr.miage.bank.repository.CarteRepository;
import fr.miage.bank.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository pRepository;
    private final CarteRepository cRepository;

    public Iterable<Paiement> findAllByCarteId(String carteId){
        return pRepository.getAllByCarte_Id(carteId);
    }

    public Iterable<Paiement> findAllByCarte(Carte carte){
        return pRepository.getAllByCarte(carte);
    }

    public Optional<Paiement> findByIdAndCarteIdAndAccountId(String paiementId, String carteId, String accountId){
        Optional<Carte> optionalCarte = cRepository.findByIdAndAccount_Iban(carteId, accountId);
        return pRepository.findByIdAndCarte(paiementId, optionalCarte.get());
    }

    public Paiement createPaiement (Paiement paiement){
        return pRepository.save(paiement);
    }

    public Optional<Carte> verifyCarte(String numCarte, String cryptoCarte, Date expDate, String nomUser){
        return cRepository.findByNumeroAndCryptoAndDateExpirationAndAccount_User_Nom(numCarte, cryptoCarte, expDate, nomUser);
    }
}
