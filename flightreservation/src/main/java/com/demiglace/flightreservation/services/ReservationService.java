package com.demiglace.flightreservation.services;

import com.demiglace.flightreservation.dto.ReservationRequest;
import com.demiglace.flightreservation.entities.Reservation;

public interface ReservationService {
	public Reservation bookFlight(ReservationRequest request);
}
