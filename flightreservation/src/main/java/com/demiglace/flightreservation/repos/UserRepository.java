package com.demiglace.flightreservation.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demiglace.flightreservation.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

}
