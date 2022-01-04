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
@RequestMapping(value = "/users/{userId}/accounts/{accountIban}/cartes/{carteId}/paiements")
public class PaiementController {

    private final PaiementService paiementService;
    private final CarteService carteService;
    private final AccountService accountService;

    private final PaiementAssembler assembler;
    private final PaiementValidator validator;

    @GetMapping
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllPaiementsByCarteId(@PathVariable("userId") String userId, @PathVariable("accountIban") String iban, @PathVariable("carteId") String carteId){
        Iterable<Paiement> allPaiements = paiementService.findAllByCarteId(carteId);
        return ResponseEntity.ok(allPaiements);
    }

    @GetMapping(value = "/{paiementId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")

    public ResponseEntity<?> getOnePaiementById(@PathVariable("userId") String userId, @PathVariable("accountIban") String iban, @PathVariable("carteId") String carteId,
                                                @PathVariable("paiementId") String paiementId){
        return Optional.ofNullable(paiementService.findByIdAndCarteIdAndAccountId(paiementId, carteId, iban)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> createPaiement(@RequestBody @Valid PaiementInput paiement, @PathVariable("userId") String userId, @PathVariable("accountIban") String accountIban,
                                            @PathVariable("carteId") String carteId){

        Optional<Carte> optionalCarte = carteService.findByIdAndAccountId(carteId, accountIban);
        Optional<Account> optionalAccount = accountService.findByIban(accountIban);

        Paiement paiement2save = new Paiement(
                UUID.randomUUID().toString(),
                optionalCarte.get(),
                new Timestamp(System.currentTimeMillis()),
                paiement.getMontant(),
                paiement.getPays(),
                optionalAccount.get()
        );

        Paiement saved = paiementService.createPaiement(paiement2save);

        URI location = linkTo(methodOn(PaiementController.class).getOnePaiementById(userId, accountIban, carteId, saved.getId())).toUri();

        return ResponseEntity.created(location).build();
    }
}
