package fr.miage.bank.service;

import fr.miage.bank.entity.Operation;
import fr.miage.bank.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository oRepository;

    public Iterable<Operation> findAllOperationsByAccountId(String accountId){
        return oRepository.findAllByCompteOwner_Id(accountId);
    }
}
