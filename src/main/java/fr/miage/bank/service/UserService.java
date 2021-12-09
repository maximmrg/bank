package fr.miage.bank.service;

import fr.miage.bank.entity.User;
import fr.miage.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository uRepository;

    public Iterable<User> findAll() {return uRepository.findAll();}

    public Optional<User> findById(String userId){
        return uRepository.findById(userId);
    }
}
