package fr.miage.bank.controller;

import fr.miage.bank.assembler.UserAssembler;
import fr.miage.bank.entity.User;
import fr.miage.bank.service.UserService;
import fr.miage.bank.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@ExposesResourceFor(User.class)
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final UserAssembler assembler;
    private final UserValidator validator;

    public UserController(UserService userService, UserAssembler assembler, UserValidator validator) {
        this.userService = userService;
        this.assembler = assembler;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        Iterable<User> allUsers = userService.findAll();
        return ResponseEntity.ok(assembler.toCollectionModel(allUsers));
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<?> getOneUserById(@PathVariable("userId") String id){
        return Optional.ofNullable(userService.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }
}
