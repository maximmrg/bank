package fr.miage.bank.controller;

import fr.miage.bank.assembler.AccountAssembler;
import fr.miage.bank.entity.*;
import fr.miage.bank.input.AccountInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.UserService;
import fr.miage.bank.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
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
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@ExposesResourceFor(Account.class)
@RequestMapping(value = "/users/{userId}/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final AccountAssembler assembler;
    private final AccountValidator validator;

    public AccountController(AccountService accountService, UserService userService, AccountAssembler assembler, AccountValidator validator) {
        this.accountService = accountService;
        this.userService = userService;
        this.assembler = assembler;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<?> getAllAccountsByUserId(@PathVariable("userId") String userId){
        Iterable<Account> allAccounts = accountService.findAllByUserId(userId);
        return ResponseEntity.ok(assembler.toCollectionModel(allAccounts));
    }

    @GetMapping(value = "/{accountId}")
    public ResponseEntity<?> getOneAccountById(@PathVariable("userId") String userId, @PathVariable("accountId") String iban){
        return Optional.ofNullable(accountService.findByUserIdAndIban(userId, iban)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveAccount(@PathVariable("userId") String userId, @RequestBody @Valid AccountInput account){

        Optional<User> optionUser = userService.findById(userId);

        Account account2save = new Account(
                account.getIBAN(),
                account.getPays(),
                account.getSecret(),
                account.getSolde(),
                optionUser.get()
        );

        Account saved = accountService.createAccount(account2save);

        //URI location = linkTo(AccountController.class).slash(saved.getIban()).toUri();
        //return ResponseEntity.created(location).build();

        URI location = linkTo(methodOn(AccountController.class).getOneAccountById(userId, saved.getIban())).toUri();
        return ResponseEntity.created(location).build();

        //return ResponseEntity.ok(saved);
    }

    @PutMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccount(@RequestBody Account account, @PathVariable("userId") String userId, @PathVariable("accountId") String accountIban){
        Optional<Account> body = Optional.ofNullable(account);

        if(!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        if(!accountService.existById(accountIban)){
            return ResponseEntity.notFound().build();
        }

        account.setIban(accountIban);
        Account result = accountService.updateAccount(account);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccountPartiel(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban,
                                                  @RequestBody Map<Object, Object> fields) {

        Optional<Account> body = accountService.findByUserIdAndIban(userId, accountIban);

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

            validator.validate(new AccountInput(account.getIban(), account.getPays(),
                    account.getSecret(), account.getSolde()));
            account.setIban(accountIban);
            accountService.updateAccount(account);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
