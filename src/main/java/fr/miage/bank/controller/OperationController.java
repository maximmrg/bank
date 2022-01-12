package fr.miage.bank.controller;

import fr.miage.bank.assembler.OperationAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.input.OperationInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.ExposesResourceFor;
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
@RequiredArgsConstructor
@ExposesResourceFor(Operation.class)
@RequestMapping(value = "users/{userId}/accounts/{accountId}/operations")
public class OperationController {
    private final OperationService operationService;
    private final AccountService accountService;

    private final OperationAssembler assembler;

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
        Optional<Account> optionalAccountDeb = accountService.findById(accountId);
        Account accountDeb = optionalAccountDeb.get();

        Optional<Account> optionalAccountCred = accountService.findByIban(operation.getCompteCrediteurIban());
        Account accountCred = optionalAccountCred.get();

        double taux = 1;

        if(accountDeb.getSolde() >= operation.getMontant()) {

            Operation operation2save = new Operation(
                    UUID.randomUUID().toString(),
                    new Timestamp(System.currentTimeMillis()),
                    operation.getLibelle(),
                    operation.getMontant(),
                    taux,
                    accountDeb,
                    accountCred,
                    operation.getCateg()
            );

            /*Operation operation2saveCred = new Operation(
                    UUID.randomUUID().toString(),
                    new Timestamp(System.currentTimeMillis()),
                    operation.getLibelle(),
                    operation.getMontant(),
                    taux,
                    accountCred,
                    accountDeb,
                    operation.getCateg()
            );*/

            Operation saved = operationService.createOperation(operation2save);
            //Operation savedCred = operationService.createOperation(operation2saveCred);
            accountService.debiterAccount(accountDeb, operation.getMontant());
            accountService.crediterAccount(accountCred, operation.getMontant(), taux );

            return ResponseEntity.ok(saved);
        }

        return ResponseEntity.badRequest().build();
    }
}
