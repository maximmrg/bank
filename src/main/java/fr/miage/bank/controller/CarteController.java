package fr.miage.bank.controller;

import fr.miage.bank.assembler.CarteAssembler;
import fr.miage.bank.entity.Account;
import fr.miage.bank.entity.Carte;
import fr.miage.bank.input.CarteInput;
import fr.miage.bank.service.AccountService;
import fr.miage.bank.service.CarteService;
import fr.miage.bank.validator.AccountValidator;
import fr.miage.bank.validator.CarteValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "users/{userId}/accounts/{accountId}/cartes")
@ExposesResourceFor(Carte.class)
public class CarteController {
    private final CarteService carteService;
    private final AccountService accountService;
    private final CarteAssembler assembler;
    private final CarteValidator validator;


    @GetMapping
    public ResponseEntity<?> getAllCartesByAccountId(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId){
        Iterable<Carte> allCartes = carteService.findAllCartesByAccountId(accountId);
        return ResponseEntity.ok(assembler.toCollectionModel(allCartes));
    }

    @GetMapping(value = "/{carteId}")
    public ResponseEntity<?> getOneCarteByIdAndAccountId(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId, @PathVariable("carteId") String carteId){
        return Optional.ofNullable(carteService.findByIdAndAccountId(carteId, accountId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createCarte(@RequestBody @Valid CarteInput carte, @PathVariable("userId") String userId, @PathVariable("accountId") String accountId){
        Optional<Account> optionalAccount = accountService.findById(accountId);

        Account account = optionalAccount.get();

        Carte carte2save = new Carte(
                UUID.randomUUID().toString(),
                carte.getCode(),
                carte.getCrypto(),
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

        return ResponseEntity.ok(saved);
    }

    @PutMapping(value = "/{carteId}")
    @Transactional
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
    public ResponseEntity<?> deleteCarte(@PathVariable("userId") String userId, @PathVariable("accountId") String accountIban, @PathVariable("carteId") String carteId){
        Optional<Carte> carte = carteService.findByIdAndAccountId(carteId, accountIban);
        if(carte.isPresent()){
            carteService.deleteCarte(carte.get());
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{carteId}")
    @Transactional
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

            validator.validate(new CarteInput(carte.getCode(), carte.getCrypto(), carte.isBloque(),
                    carte.isLocalisation(), carte.getPlafond(), carte.isSansContact(),
                    carte.isVirtual()));

            carte.setId(carteId);
            carteService.updateCarte(carte);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
