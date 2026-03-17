package com.example.minimarketplaceprototype.repository;

import com.example.minimarketplaceprototype.model.Role;
import com.example.minimarketplaceprototype.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}