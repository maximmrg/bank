package fr.miage.bank.assembler;

import fr.miage.bank.controller.AccountController;
import fr.miage.bank.controller.CarteController;
import fr.miage.bank.entity.Carte;
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
public class CarteAssembler implements RepresentationModelAssembler<Carte, EntityModel<Carte>> {
    @Override
    public EntityModel<Carte> toModel(Carte entity) {
        String userId = entity.getAccount().getUser().getId();
        return EntityModel.of(entity,
                linkTo(methodOn(CarteController.class)
                        .getOneCarteByIdAndAccountId(userId, entity.getAccount().getIban(), entity.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class)
                        .getOneAccountById(userId, entity.getAccount().getIban())).withRel("account"));
    }

    public CollectionModel<EntityModel<Carte>> toCollectionModel(Iterable<? extends Carte> entities, String userId, String iban) {

        List<EntityModel<Carte>> carteModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());

        return CollectionModel.of(carteModel,
                linkTo(methodOn(CarteController.class)
                        .getAllCartesByAccountId(userId, iban)).withSelfRel());
    }
}
