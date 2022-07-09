package com.demiglace.flightreservation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.demiglace.flightreservation.dto.ReservationRequest;
import com.demiglace.flightreservation.entities.Flight;
import com.demiglace.flightreservation.entities.Reservation;
import com.demiglace.flightreservation.repos.FlightRepository;
import com.demiglace.flightreservation.services.ReservationService;

@Controller
public class ReservationController {
	@Autowired
	FlightRepository flightRepository;

	@Autowired
	ReservationService reservationService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);

	@RequestMapping("/showCompleteReservation")
	public String showCompleteReservation(@RequestParam("flightId") Long flightId, ModelMap modelMap) {
		Flight flight = flightRepository.findById(flightId).get();
		LOGGER.info("showCompleteReservation() invoked with flightId" + flightId);
		modelMap.addAttribute("flight", flight);
		LOGGER.info("flight is " + flight );
		return "completeReservation";
	}

	@RequestMapping(value = "/completeReservation", method = RequestMethod.POST)
	public String completeReservation(ReservationRequest request, ModelMap modelMap) {
		LOGGER.info("completeReservation()" + request);
		Reservation reservation = reservationService.bookFlight(request);
		modelMap.addAttribute("msg", "Reservation Created Successfully and the id is " + reservation.getId());
		return "reservationConfirmation";
	}
}
