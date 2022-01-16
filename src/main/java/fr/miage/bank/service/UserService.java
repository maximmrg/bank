package fr.miage.bank.service;

import fr.miage.bank.entity.Role;
import fr.miage.bank.entity.User;
import fr.miage.bank.repository.RoleRepository;
import fr.miage.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.SimpleTimeZone;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository uRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public Iterable<User> findAll() {return uRepository.findAll();}

    public Optional<User> findById(String userId){
        return uRepository.findById(userId);
    }

    public boolean existById(String id) {return uRepository.existsById(id);}

    public User createUser(User user) {return uRepository.save(user);}

    public User updateUser(User user) {return uRepository.save(user);}

    public Optional<User> getUserByEmail(String email) {return uRepository.findByEmail(email);}

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = uRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            throw new UsernameNotFoundException("User not found in the database");
        }

        User user = optionalUser.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public void addRoleToUser(User user, String roleName){
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
        uRepository.save(user);
    }

    public void addRoleToUser(String email, String roleName){
        Optional<User> user = uRepository.findByEmail(email);
        Role role = roleRepository.findByName(roleName);
        if(user.isPresent()) {
            user.get().getRoles().add(role);
        }
    }

    public User saveUserWithPasswordChanged(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return uRepository.save(user);
    }

    public boolean existMail(String email) {
        return uRepository.existsByEmail(email);
    }


}
