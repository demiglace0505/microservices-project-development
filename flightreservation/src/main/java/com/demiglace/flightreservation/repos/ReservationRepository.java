package com.demiglace.flightreservation.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demiglace.flightreservation.entities.Flight;
import com.demiglace.flightreservation.entities.Reservation;
import com.demiglace.flightreservation.entities.User;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
