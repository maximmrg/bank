package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
