package fr.miage.bank.controller;

import fr.miage.bank.assembler.UserAssembler;
import fr.miage.bank.entity.User;
import fr.miage.bank.input.UserInput;
import fr.miage.bank.service.UserService;
import fr.miage.bank.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@RestController
@ExposesResourceFor(User.class)
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserAssembler assembler;
    private final UserValidator validator;

    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        Iterable<User> allUsers = userService.findAll();
        return ResponseEntity.ok(assembler.toCollectionModel(allUsers));
    }

    @GetMapping(value = "/{userId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> getOneUserById(@PathVariable("userId") String userId){
        return Optional.ofNullable(userService.findById(userId)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveUser(@RequestBody @Valid UserInput user){
        User user2save = new User(
                UUID.randomUUID().toString(),
                user.getNom(),
                user.getPrenom(),
                user.getBirthDate(),
                user.getNoPasseport(),
                user.getNumTel(),
                user.getEmail(),
                passwordEncoder.encode(user.getPassword())
        );

        User saved = userService.createUser(user2save);

        URI location = linkTo(UserController.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{userId}")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("userId") String userId){
        Optional<User> body = Optional.ofNullable(user);

        if(!body.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        if(!userService.existById(userId)){
            return ResponseEntity.notFound().build();
        }

        user.setId(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User result = userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{userId}")
    @Transactional
    @PreAuthorize("hasPermission(#userId, 'User', 'MANAGE_USER')")
    public ResponseEntity<?> updateUserPartial(@PathVariable("userId") String userId,
                                               @RequestBody Map<Object, Object> fields){

        Optional<User> body = userService.findById(userId);

        if(body.isPresent()){
            User user = body.get();

            fields.forEach((f,v) -> {
                Field field = ReflectionUtils.findField(User.class, f.toString());
                field.setAccessible(true);

                if(field.getType() == Date.class){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
                    try {
                        ReflectionUtils.setField(field, user, formatter.parse(v.toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    ReflectionUtils.setField(field, user, v);
                }
            });

            validator.validate(new UserInput(user.getNom(), user.getPrenom(), user.getBirthDate(),
                    user.getNoPasseport(), user.getNumTel(), user.getEmail(), user.getPassword()));

            user.setId(userId);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.updateUser(user);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}
