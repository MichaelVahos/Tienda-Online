package com.tienda.app.repositories;

import com.tienda.app.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Role, Long> {
}
