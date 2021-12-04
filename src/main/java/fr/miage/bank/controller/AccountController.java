package fr.miage.bank.controller;

import fr.miage.bank.assembler.AccountAssembler;
import fr.miage.bank.entity.*;
import fr.miage.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@ExposesResourceFor(Account.class)
@RequestMapping(value = "/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountAssembler assembler;
    private final AccountValidator validator;

    public AccountController(AccountService accountService, AccountAssembler assembler, AccountValidator validator) {
        this.accountService = accountService;
        this.assembler = assembler;
        this.validator = validator;
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

    @PatchMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccountPartiel(@PathVariable("accountId") String accountId,
                                                  @RequestBody Map<Object, Object> fields) {

        Optional<Account> body = accountService.findById(accountId);

        if(body.isPresent()) {
            Account account = body.get();

            fields.forEach((f,v) -> {
                Field field = ReflectionUtils.findField(Account.class, f.toString());
                field.setAccessible(true);

                if(field.getType() == Date.class){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
                    try {
                        ReflectionUtils.setField(field, account, formatter.parse(v.toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    ReflectionUtils.setField(field, account, v);
                }
            });

            validator.validate(new AccountInput(account.getNom(), account.getPrenom(), account.getBirthDate(), account.getPays(),
                    account.getNoPasseport(), account.getNumTel(), account.getSecret(), account.getIban()));
            account.setId(accountId);
            accountService.updateAccount(account);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
