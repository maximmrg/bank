package fr.miage.bank.assembler;

import fr.miage.bank.controller.CarteController;
import fr.miage.bank.controller.PaiementController;
import fr.miage.bank.entity.Paiement;
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

    public CollectionModel<EntityModel<Paiement>> toCollectionModel(Iterable<? extends Paiement> entities, String userId, String iban, String carteId) {

        List<EntityModel<Paiement>> paiementModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());

        return CollectionModel.of(paiementModel,
                linkTo(methodOn(PaiementController.class)
                        .getAllPaiementsByCarteId(userId, iban, carteId)).withSelfRel());
    }
}
