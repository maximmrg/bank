package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping(value = "users/{userId}/accounts/{accountId}/operations")
public class OperationController {
    private final OperationService operationService;
    private final OperationAssembler assembler;

    public OperationController(OperationService operationService, OperationAssembler assembler) {
        this.operationService = operationService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<?> getAllOperationsByAccountId(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @RequestParam( required = false, name = "categorie") Optional<String> categ){
        Iterable<Operation> allOperations;

        if(categ.isPresent()){
            allOperations = operationService.findAllOperationsByAccountAndCateg(userId, accountIban, categ.get());
        } else {
            allOperations = operationService.findAllOperationsByUserIdAndAccountId(userId, accountIban);
        }
        return ResponseEntity.ok(assembler.toCollectionModel(allOperations));
    }

    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOneOperationById(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId){
        return Optional.ofNullable(operationService.findByIdAndCompteOwnerId(operationId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }
}
