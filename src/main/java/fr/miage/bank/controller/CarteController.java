package fr.miage.bank.controller;

import fr.miage.bank.assembler.CarteAssembler;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.service.CarteServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getAllCartes(@PathVariable("accountId") String id){
        Iterable<Carte> allCartes = carteServices.findAllCartes(id);
        return ResponseEntity.ok(assembler.toCollectionModel(allCartes));
    }
}
