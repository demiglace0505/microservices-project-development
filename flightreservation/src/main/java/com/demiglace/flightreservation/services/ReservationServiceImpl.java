package com.demiglace.flightreservation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demiglace.flightreservation.dto.ReservationRequest;
import com.demiglace.flightreservation.entities.Flight;
import com.demiglace.flightreservation.entities.Passenger;
import com.demiglace.flightreservation.entities.Reservation;
import com.demiglace.flightreservation.repos.FlightRepository;
import com.demiglace.flightreservation.repos.PassengerRepository;
import com.demiglace.flightreservation.repos.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService {
	
	@Autowired
	FlightRepository flightRepository;
	
	@Autowired
	PassengerRepository passengerRepository;
	
	@Autowired
	ReservationRepository reservationRepository;
	
	@Override
	public Reservation bookFlight(ReservationRequest request) {
		// insert code for invoking payment gateway here
		
		// get the flight
		Long flightId = request.getFlightId();
		Flight flight = flightRepository.findById(flightId).get();
		
		// create new passenger and save to database
		Passenger passenger = new Passenger();
		passenger.setFirstName(request.getPassengerFirstName());
		passenger.setLastName(request.getPassengerLastName());
		passenger.setPhone(request.getPassengerPhone());
		passenger.setEmail(request.getPassengerEmail());
		Passenger savedPassenger = passengerRepository.save(passenger);
		
		// create the reservation and save to database
		Reservation reservation = new Reservation();
		reservation.setFlight(flight);
		reservation.setPassenger(savedPassenger);
		reservation.setCheckedIn(false);		
		Reservation savedReservation = reservationRepository.save(reservation);
		
		return savedReservation;
	}
}
