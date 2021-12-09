package fr.miage.bank.assembler;

import fr.miage.bank.controller.AccountController;
import fr.miage.bank.controller.CarteController;
import fr.miage.bank.entity.Carte;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CarteAssembler implements RepresentationModelAssembler<Carte, EntityModel<Carte>> {
    @Override
    public EntityModel<Carte> toModel(Carte entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(CarteController.class)
                        .getOneCarteByIdAndAccountId(entity.getAccount().getIban(), entity.getId())).withSelfRel(),
                linkTo(methodOn(AccountController.class)
                        .getOneAccountById(entity.getAccount().getIban())).withRel("account"));
    }

    @Override
    public CollectionModel<EntityModel<Carte>> toCollectionModel(Iterable<? extends Carte> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
