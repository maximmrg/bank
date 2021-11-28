package fr.miage.bank.controller;

import fr.miage.bank.assembler.AccountAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.AccountInput;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.Operation;
import fr.miage.bank.service.AccountService;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountAssembler assembler;

    public AccountController(AccountService accountService, AccountAssembler assembler) {
        this.accountService = accountService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<?> getAllAccounts(){
        Iterable<Account> allAccounts = accountService.findAll();
        return ResponseEntity.ok(assembler.toCollectionModel(allAccounts));
    }

    @GetMapping(value = "/{accountId}")
    public ResponseEntity<?> getOneAccountById(@PathVariable("accountId") String id){
        return Optional.ofNullable(accountService.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountInput account){
        Account account2save = new Account(
                UUID.randomUUID().toString(),
                account.getNom(),
                account.getPrenom(),
                account.getBirthDate(),
                account.getPays(),
                account.getNoPasseport(),
                account.getNumTel(),
                account.getSecret(),
                account.getIBAN()
        );

        Account saved = accountService.createAccount(account2save);

        URI location = linkTo(AccountController.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccount(@RequestBody Account account, @PathVariable("accountId") String accountId){
        Optional<Account> body = Optional.ofNullable(account);

        if(!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        if(!accountService.existById(accountId)){
            return ResponseEntity.notFound().build();
        }

        account.setId(accountId);
        Account result = accountService.updateAccount(account);
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/{accountId}/cartes")
    public ResponseEntity<?> getAllCartes(@PathVariable("accountId") String id){
        Iterable<Carte> allCartes = accountService.findAllCartes(id);
        return ResponseEntity.ok(allCartes);
    }

    @GetMapping(value = "/{accountId}/operations")
    public ResponseEntity<?> getAllOperations(@PathVariable("accountId") String id){
        Iterable<Operation> allOperations = accountService.findAllOperations(id);
        return ResponseEntity.ok(allOperations);
    }

    @GetMapping(value = "/{accountId}/operations/{operationId}")
    public ResponseEntity<?> getOperationById(@PathVariable("accountId") String accountId, @PathVariable("operationId") String operationId ){

        return Optional.ofNullable(accountService.findOperationById(operationId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(i.get()))
                .orElse(ResponseEntity.notFound().build());
    }

}
