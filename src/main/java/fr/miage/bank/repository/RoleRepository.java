package fr.miage.bank.repository;

import fr.miage.bank.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByName(String roleName);
}

