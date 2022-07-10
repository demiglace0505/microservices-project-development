package com.demiglace.flightreservation.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demiglace.flightreservation.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
