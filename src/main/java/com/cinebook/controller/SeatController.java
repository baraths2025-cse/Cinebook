

package com.cinebook.controller;

import com.cinebook.model.Booking;
import com.cinebook.repository.BookingRepository;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final BookingRepository bookingRepository;

    public SeatController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/booked/{showId}")
    public List<String> getBookedSeats(@PathVariable Long showId) {

        List<Booking> bookings =
                bookingRepository.findByShowIdAndStatus(
                        showId, "CONFIRMED");

        List<String> bookedSeats = new ArrayList<>();

        for (Booking booking : bookings) {
            String[] seats = booking.getSeats().split(",");

            for (String seat : seats) {
                bookedSeats.add(seat.trim());
            }
        }

        return bookedSeats;
    }
}
