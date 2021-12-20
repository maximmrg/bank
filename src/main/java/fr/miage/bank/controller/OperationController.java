package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.input.OperationInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "users/{userId}/accounts/{accountId}/operations")
public class OperationController {
    private final OperationService operationService;
    private final AccountService accountService;

    private final OperationAssembler assembler;

    public OperationController(OperationService operationService, AccountService accountService, OperationAssembler assembler) {
        this.operationService = operationService;
        this.accountService = accountService;
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

    @PostMapping
    @Transactional
    public ResponseEntity<?> createOperation(@RequestBody @Valid OperationInput operation, @PathVariable("userId") String userId, @PathVariable("accountId") String accountId){
        Optional<Account> optionalAccountCred = accountService.findById(accountId);
        Account accountCred = optionalAccountCred.get();

        Optional<Account> optionalAccountDeb = accountService.findByIban(operation.getCompteDebiteurId());
        Account accountDeb = optionalAccountDeb.get();

        Operation operation2save = new Operation(
                UUID.randomUUID().toString(),
                new Timestamp(System.currentTimeMillis()),
                operation.getLibelle(),
                operation.getMontant(),
                operation.getTaux(),
                accountDeb,
                accountCred,
                operation.getCateg(),
                operation.getPays()
        );

        Operation saved = operationService.createOperation(operation2save);

        return ResponseEntity.ok(saved);
    }
}
