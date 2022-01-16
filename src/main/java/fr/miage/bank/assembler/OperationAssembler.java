package fr.miage.bank.assembler;

import fr.miage.bank.controller.AccountController;
import fr.miage.bank.controller.OperationController;
import fr.miage.bank.entity.Operation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OperationAssembler implements RepresentationModelAssembler<Operation, EntityModel<Operation>> {
    @Override
    public EntityModel<Operation> toModel(Operation entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(AccountController.class)
                        .getOneAccountById(entity.getCompteCrediteur().getUser().getId(), entity.getCompteCrediteur().getIban())).withRel("account"));
    }

    public CollectionModel<EntityModel<Operation>> toCollectionModel(Iterable<? extends Operation> entities, String userId, String iban) {

        List<EntityModel<Operation>> operationModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());

        return CollectionModel.of(operationModel,
                linkTo(methodOn(OperationController.class)
                        .getAllOperationsByAccountId(userId, iban, null)).withSelfRel());
    }
}
