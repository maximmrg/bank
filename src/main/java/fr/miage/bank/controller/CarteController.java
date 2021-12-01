package fr.miage.bank.controller;

import fr.miage.bank.assembler.CarteAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.CarteInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CarteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/accounts/{accountId}/cartes")
public class CarteController {
    private final CarteService carteService;
    private final AccountService accountService;
    private final CarteAssembler assembler;

    @GetMapping
    public ResponseEntity<?> getAllCartesByAccountId(@PathVariable("accountId") String accountId){
        Iterable<Carte> allCartes = carteService.findAllCartesByAccountId(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allCartes));
    }

    @GetMapping(value = "/{carteId}")
    public ResponseEntity<?> getOneCarteByIdAndAccountId(@PathVariable("accountId") String accountId, @PathVariable("carteId") String carteId){
        return Optional.ofNullable(carteService.findByIdAndAccountId(carteId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createCarte(@RequestBody @Valid CarteInput carte, @PathVariable("accountId") String accountId){
        Optional<Account> account = accountService.findById(accountId);

        Carte carte2save = new Carte(
                UUID.randomUUID().toString(),
                Integer.parseInt(carte.getCode()),
                Integer.parseInt(carte.getCrypto()),
                carte.isBloque(),
                carte.isLocalisation(),
                carte.getPlafond(),
                carte.isSansContact(),
                carte.isVirtual(),
                account.get()
        );

        Carte saved = carteService.createCarte(carte2save);

        URI location = linkTo(CarteController.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
