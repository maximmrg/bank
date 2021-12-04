package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping(value = "/accounts/{accountId}/operations")
public class OperationController {
    private final OperationService operationService;
    private final OperationAssembler assembler;

    public OperationController(OperationService operationService, OperationAssembler assembler) {
        this.operationService = operationService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<?> getAllOperationsByAccountId(@PathVariable("accountId") String accountId){
        Iterable<Operation> allOperations = operationService.findAllOperationsByAccountId(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allOperations));
    }

    @GetMapping(value = "/{operationId}")
    public ResponseEntity<?> getOneOperationById(@PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId){
        return Optional.ofNullable(operationService.findByIdAndCompteOwnerId(operationId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }
}
