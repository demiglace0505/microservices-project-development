package com.demiglace.flightcheckin.integration;

import com.demiglace.integration.dto.Reservation;
import com.demiglace.integration.dto.ReservationUpdateRequest;

public interface ReservationRestClient {
	public Reservation findReservation(Long id);
	
	public Reservation updateReservation(ReservationUpdateRequest request);
}
