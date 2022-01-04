package fr.miage.bank.assembler;

import fr.miage.bank.controller.CarteController;
import fr.miage.bank.controller.PaiementController;
import fr.miage.bank.entity.Paiement;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaiementAssembler implements RepresentationModelAssembler<Paiement, EntityModel<Paiement>> {

    @Override
    public EntityModel<Paiement> toModel(Paiement entity) {
        String userId = entity.getCarte().getAccount().getUser().getId();
        String iban = entity.getCarte().getAccount().getIban();
        String carteId = entity.getCarte().getId();

        return EntityModel.of(entity,
                linkTo(methodOn(PaiementController.class)
                        .getOnePaiementById(userId, iban, carteId, entity.getId())).withSelfRel(),
                linkTo(methodOn(CarteController.class)
                        .getOneCarteByIdAndAccountId(userId, iban, carteId)).withRel("carte"));
    }

    @Override
    public CollectionModel<EntityModel<Paiement>> toCollectionModel(Iterable<? extends Paiement> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
