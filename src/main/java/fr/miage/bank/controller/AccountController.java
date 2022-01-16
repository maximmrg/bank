package fr.miage.bank.controller;

import fr.miage.bank.BankApplication;
import fr.miage.bank.assembler.AccountAssembler;
import fr.miage.bank.entity.*;
import fr.miage.bank.input.AccountInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.UserService;
import fr.miage.bank.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor
@ExposesResourceFor(Account.class)
@RequestMapping(value = "/users/{userId}/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final AccountAssembler assembler;
    private final AccountValidator validator;

    @GetMapping
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER') || hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllAccountsByUserId(@PathVariable("userId") String userId){
        Iterable<Account> allAccounts = accountService.findAllByUserId(userId);
        return ResponseEntity.ok(assembler.toCollectionModel(allAccounts, userId));
    }

    @GetMapping(value = "/{accountId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER') || hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getOneAccountById(@PathVariable("userId") String userId, @PathVariable("accountId") String iban){
        return Optional.ofNullable(accountService.findByUserIdAndIban(userId, iban)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER') || hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveAccount(@PathVariable("userId") String userId, @RequestBody @Valid AccountInput account){

        Optional<User> optionUser = userService.findById(userId);

        String pays = account.getPays();

        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.getByCode(BankApplication.countries.get(pays)))
                .buildRandom();

        boolean existIban = accountService.existById(iban.toString());

        while(existIban){
            iban = new Iban.Builder()
                    .countryCode(CountryCode.getByCode(BankApplication.countries.get(pays)))
                    .buildRandom();

            existIban = accountService.existById(iban.toString());
        }

        Account account2save = new Account(
                iban.toString(),
                pays,
                account.getSecret(),
                account.getSolde(),
                optionUser.get()
        );

        Account saved = accountService.createAccount(account2save);

        URI location = linkTo(methodOn(AccountController.class).getOneAccountById(userId, saved.getIban())).toUri();
        return ResponseEntity.created(location).build();
    }

    @PatchMapping(value = "/{accountId}")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER') || hasRole('ROLE_ADMIN')")
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

            validator.validate(new AccountInput(account.getPays(),
                    account.getSecret(), account.getSolde()));
            account.setIban(accountIban);
            accountService.updateAccount(account);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
