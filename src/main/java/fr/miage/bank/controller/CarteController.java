package fr.miage.bank.controller;

import fr.miage.bank.assembler.CarteAssembler;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.service.CarteServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/accounts/{accountId}/cartes")
public class CarteController {
    private final CarteServices carteServices;
    private final CarteAssembler assembler;

    public CarteController(CarteServices carteServices, CarteAssembler assembler) {
        this.carteServices = carteServices;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<?> getAllCartesByAccountId(@PathVariable("accountId") String accountId){
        Iterable<Carte> allCartes = carteServices.findAllCartesByAccountId(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allCartes));
    }

    @GetMapping(value = "/{carteId}")
    public ResponseEntity<?> getOneCarteByIdAndAccountId(@PathVariable("accountId") String accountId, @PathVariable("carteId") String carteId){
        return Optional.ofNullable(carteServices.findByIdAndAccountId(carteId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }
}
