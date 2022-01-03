package fr.miage.bank.controller;

import com.mifmif.common.regex.Generex;
import fr.miage.bank.assembler.CarteAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.CarteInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CarteService;
import fr.miage.bank.service.UserService;
import fr.miage.bank.validator.AccountValidator;
import fr.miage.bank.validator.CarteValidator;
import lombok.RequiredArgsConstructor;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "users/{userId}/accounts/{accountId}/cartes")
@ExposesResourceFor(Carte.class)
public class CarteController {

    private final CarteService carteService;
    private final AccountService accountService;
    private final UserService userService;

    private final CarteAssembler assembler;
    private final CarteValidator validator;


    @GetMapping
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getAllCartesByAccountId(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId){
        Iterable<Carte> allCartes = carteService.findAllCartesByAccountId(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allCartes));
    }

    @GetMapping(value = "/{carteId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneCarteByIdAndAccountId(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String carteId){
        return Optional.ofNullable(carteService.findByIdAndAccountId(carteId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> createCarte(@RequestBody @Valid CarteInput carte, @PathVariable("userId") String userId, @PathVariable("accountId") String accountId){
        Optional<Account> optionalAccount = accountService.findById(accountId);

        Account account = optionalAccount.get();

        Generex genrexCarteNum = new Generex("([0-9]{16})");
        Generex genrexCarteCode = new Generex("([0-9]{4})");
        Generex genrexCrypto = new Generex("([0-9]{3})");

        Carte carte2save = new Carte(
                genrexCarteNum.random(),
                genrexCarteNum.random(),
                genrexCarteCode.random(),
                genrexCrypto.random(),
                carte.isBloque(),
                carte.isLocalisation(),
                carte.getPlafond(),
                carte.isSansContact(),
                carte.isVirtual(),
                account
        );

        Carte saved = carteService.createCarte(carte2save);

        //Link location = linkTo(CarteController.class).slash(saved.getId()).slash(accountId).withSelfRel();
        //return ResponseEntity.ok(location.withSelfRel());

        URI location = linkTo(methodOn(CarteController.class).getOneCarteByIdAndAccountId(userId, accountId, saved.getId())).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{carteId}")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateCarte(@RequestBody Carte carte, @PathVariable("carteId") String carteId){
        Optional<Carte> body = Optional.ofNullable(carte);

        if(!body.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        if(!carteService.existById(carteId)){
            return ResponseEntity.notFound().build();
        }

        carte.setId(carteId);
        Carte result = carteService.updateCarte(carte);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{carteId}")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> deleteCarte(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("carteId") String carteId){
        Optional<Carte> carte = carteService.findByIdAndAccountId(carteId, accountIban);
        if(carte.isPresent()){
            carteService.deleteCarte(carte.get());
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{carteId}")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateCartePartial(@PathVariable("accountId") String accoundIban,
                                                @PathVariable("carteId") String carteId,
                                                @RequestBody Map<Object, Object> fields){
        Optional<Carte> body = carteService.findByIdAndAccountId(carteId, accoundIban);

        if(body.isPresent()){
            Carte carte = body.get();

            fields.forEach((f,v) -> {
                Field field = ReflectionUtils.findField(Carte.class, f.toString());
                field.setAccessible(true);

                if(field.getType() == Date.class){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
                    try {
                        ReflectionUtils.setField(field, carte, formatter.parse(v.toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else{
                    ReflectionUtils.setField(field, carte, v);
                }
            });

            validator.validate(new CarteInput(carte.isBloque(),
                    carte.isLocalisation(), carte.getPlafond(), carte.isSansContact(),
                    carte.isVirtual()));

            carte.setId(carteId);
            carteService.updateCarte(carte);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/{carteId}/block")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> blockCarte(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("carteId") String carteId) {

        Optional<Account> optionAccount = accountService.findByUserIdAndIban(userId, accountIban);
        if(!optionAccount.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Optional<Carte> optionCarte = carteService.findByIdAndAccountId(carteId, accountIban);
        if(!optionCarte.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Carte carte = optionCarte.get();
        Carte carte2save = carteService.blockCarte(carte);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{carteId}/activeLocalisation")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> activeLocalisation(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("carteId") String carteId){
        Optional<Account> optionAccount = accountService.findByUserIdAndIban(userId, accountIban);
        if(!optionAccount.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Optional<Carte> optionCarte = carteService.findByIdAndAccountId(carteId, accountIban);
        if(!optionCarte.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Carte carte = optionCarte.get();
        Carte carte2save = carteService.activeLocalisation(carte);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{carteId}/setPlafond")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> setPlafond(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("carteId") String carteId,
                                           @RequestParam("plafond") int plafond) {
        Optional<Account> optionAccount = accountService.findByUserIdAndIban(userId, accountIban);
        if(!optionAccount.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Optional<Carte> optionCarte = carteService.findByIdAndAccountId(carteId, accountIban);
        if(!optionCarte.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Carte carte = optionCarte.get();
        Carte carte2save = carteService.setPlafond(carte, plafond);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{carteId}/setContact")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> setSansContact(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("carteId") String carteId){

        Optional<Account> optionAccount = accountService.findByUserIdAndIban(userId, accountIban);
        if(!optionAccount.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Optional<Carte> optionCarte = carteService.findByIdAndAccountId(carteId, accountIban);
        if(!optionCarte.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Carte carte = optionCarte.get();
        Carte carte2save = carteService.setSansContact(carte);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{carteId}/unsetContact")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> unsetSansContact(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("carteId") String carteId){

        Optional<Account> optionAccount = accountService.findByUserIdAndIban(userId, accountIban);
        if(!optionAccount.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Optional<Carte> optionCarte = carteService.findByIdAndAccountId(carteId, accountIban);
        if(!optionCarte.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Carte carte = optionCarte.get();
        Carte carte2save = carteService.unsetSansContact(carte);

        return ResponseEntity.noContent().build();
    }
}
