package fr.miage.bank.assembler;

import fr.miage.bank.controller.AccountController;
import fr.miage.bank.controller.CarteController;
import fr.miage.bank.controller.OperationController;
import fr.miage.bank.entity.Account;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountAssembler implements RepresentationModelAssembler<Account, EntityModel<Account>> {
    @Override
    public EntityModel<Account> toModel(Account entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(AccountController.class)
                        .getOneAccountById(entity.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class)
                        .getAllAccounts()).withRel("collection"),
                linkTo(methodOn(CarteController.class)
                        .getAllCartesByAccountId(entity.getId())).withRel("cartes"),
                linkTo(methodOn(OperationController.class).getAllOperationsByAccountId(entity.getId())).withRel("operations"));
    }

    @Override
    public CollectionModel<EntityModel<Account>> toCollectionModel(Iterable<? extends Account> entities) {
        List<EntityModel<Account>> accountModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());

        return CollectionModel.of(accountModel,
                linkTo(methodOn(AccountController.class)
                        .getAllAccounts()).withSelfRel());
    }
}
