package com.demiglace.flightreservation.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demiglace.flightreservation.controllers.ReservationController;
import com.demiglace.flightreservation.dto.ReservationRequest;
import com.demiglace.flightreservation.entities.Flight;
import com.demiglace.flightreservation.entities.Passenger;
import com.demiglace.flightreservation.entities.Reservation;
import com.demiglace.flightreservation.repos.FlightRepository;
import com.demiglace.flightreservation.repos.PassengerRepository;
import com.demiglace.flightreservation.repos.ReservationRepository;
import com.demiglace.flightreservation.util.EmailUtil;
import com.demiglace.flightreservation.util.PDFGenerator;

@Service
public class ReservationServiceImpl implements ReservationService {

	@Value("${com.demiglace.flightreservation.itinerary.dirpath}")
	private String ITINERARY_DIR;

	@Autowired
	FlightRepository flightRepository;

	@Autowired
	PassengerRepository passengerRepository;

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	PDFGenerator pdfGenerator;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

//	@Autowired
//	EmailUtil emailUtil;

	@Override
	@Transactional
	public Reservation bookFlight(ReservationRequest request) {
		LOGGER.info("inside bookFlight()");
		// insert code for invoking payment gateway here

		// get the flight
		Long flightId = request.getFlightId();
		LOGGER.info("fetching flight for flight id:" + flightId);
		Flight flight = flightRepository.findById(flightId).get();

		// create new passenger and save to database
		Passenger passenger = new Passenger();
		passenger.setFirstName(request.getPassengerFirstName());
		passenger.setLastName(request.getPassengerLastName());
		passenger.setPhone(request.getPassengerPhone());
		passenger.setEmail(request.getPassengerEmail());
		LOGGER.info("saving passenger: " + passenger);
		Passenger savedPassenger = passengerRepository.save(passenger);

		// create the reservation and save to database
		Reservation reservation = new Reservation();
		reservation.setFlight(flight);
		reservation.setPassenger(savedPassenger);
		reservation.setCheckedIn(false);
		LOGGER.info("saving reservation: " + reservation);
		Reservation savedReservation = reservationRepository.save(reservation);

		// generate itinerary from the reservation
		String filePath = ITINERARY_DIR + savedReservation.getId() + ".pdf";
		LOGGER.info("generating itinerary");
		pdfGenerator.generateItinerary(savedReservation,
				filePath);
		
		// send email
//		emailUtil.sendItinerary(passenger.getEmail(), filePath);

		return savedReservation;
	}
}
