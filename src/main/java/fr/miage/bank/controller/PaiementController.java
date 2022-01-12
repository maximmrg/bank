package fr.miage.bank.controller;

import fr.miage.bank.assembler.PaiementAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.Paiement;
import fr.miage.bank.input.PaiementInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CarteService;
import fr.miage.bank.service.PaiementService;
import fr.miage.bank.validator.PaiementValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@ExposesResourceFor(Paiement.class)
public class PaiementController {

    private final PaiementService paiementService;
    private final CarteService carteService;
    private final AccountService accountService;

    private final PaiementAssembler assembler;
    private final PaiementValidator validator;

    @GetMapping("/users/{userId}/accounts/{accountIban}/cartes/{carteId}/paiements")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllPaiementsByCarteId(@PathVariable("userId") String userId, @PathVariable("accountIban") String iban, @PathVariable("carteId") String carteId){
        Iterable<Paiement> allPaiements = paiementService.findAllByCarteId(carteId);
        return ResponseEntity.ok(allPaiements);
    }

    @GetMapping(value = "/users/{userId}/accounts/{accountIban}/cartes/{carteId}/paiements/{paiementId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")

    public ResponseEntity<?> getOnePaiementById(@PathVariable("userId") String userId, @PathVariable("accountIban") String iban, @PathVariable("carteId") String carteId,
                                                @PathVariable("paiementId") String paiementId){
        return Optional.ofNullable(paiementService.findByIdAndCarteIdAndAccountId(paiementId, carteId, iban)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/paiements")
    @Transactional
    public ResponseEntity<?> createPaiement(@RequestBody @Valid PaiementInput paiement){

        Optional<Carte> optionalCarte = paiementService.verifyCarte(paiement.getNumCarte(), paiement.getCryptoCarte(), paiement.getDateExpCarte(), paiement.getNomUser());

        //Si la carte existe, donc que les infos sont correctes
        if(optionalCarte.isPresent()){
            Carte carte = optionalCarte.get();
            Account compteDeb = carte.getAccount();

            Optional<Account> optionalAccount = accountService.findByIban(paiement.getIbanCrediteur());
            Account compteCred = optionalAccount.get();

            if(carte.isLocalisation()){
                String paysDeb = compteDeb.getPays();
                String paysCred = compteCred.getPays();

                if(paysDeb != paysCred){
                    return ResponseEntity.badRequest().build();
                }
            }

            double taux = 1;

            if(compteDeb.getSolde() >= paiement.getMontant()) {

                Paiement paiement2save = new Paiement(
                        UUID.randomUUID().toString(),
                        optionalCarte.get(),
                        new Timestamp(System.currentTimeMillis()),
                        paiement.getMontant(),
                        taux,
                        paiement.getPays(),
                        compteCred
                );

                Paiement saved = paiementService.createPaiement(paiement2save);
                accountService.debiterAccount(compteDeb, paiement.getMontant());
                accountService.crediterAccount(compteCred, paiement.getMontant(), taux);

                if (carte.isVirtual()) {
                    carteService.deleteCarte(carte);
                }

                URI location = linkTo(methodOn(PaiementController.class).getOnePaiementById(carte.getAccount().getUser().getId(), carte.getAccount().getIban(), carte.getId(), saved.getId())).toUri();

                return ResponseEntity.created(location).build();
            }
        }

        return ResponseEntity.badRequest().build();
    }
}
