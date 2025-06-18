package com.bharath.springcloud.repos;

import com.bharath.springcloud.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
