
package com.cinebook.controller;

import com.cinebook.model.Booking;
import com.cinebook.model.Movie;
import com.cinebook.model.MovieShow;
import com.cinebook.model.User;
import com.cinebook.repository.BookingRepository;
import com.cinebook.repository.MovieShowRepository;
import com.cinebook.repository.MovieRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final MovieShowRepository showRepository;
    private final MovieRepository movieRepository;

    public BookingController(
            BookingRepository bookingRepository,
            MovieShowRepository showRepository,
            MovieRepository movieRepository) {

        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
    }

    @PostMapping
    public ResponseEntity<?> bookSeats(
            @RequestBody Booking booking,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        MovieShow show = showRepository.findById(booking.getShowId())
                .orElse(null);

        if (show == null) {
            return ResponseEntity.badRequest().body("Show not found");
        }
        if (show.getShowDate() == null || show.getShowTime() == null) {
    return ResponseEntity.badRequest()
            .body("Show date and time are unavailable");
}

LocalDateTime showDateTime = LocalDateTime.of(
        show.getShowDate(),
        show.getShowTime()
);

if (!showDateTime.isAfter(LocalDateTime.now())) {
    return ResponseEntity.badRequest()
            .body("This show has already started. Booking is closed.");
}

        if (booking.getSeats() == null ||
                booking.getSeats().isBlank()) {
            return ResponseEntity.badRequest().body("Select at least one seat");
        }

        String[] selectedSeats = booking.getSeats().split(",");

        if (selectedSeats.length > show.getTotalSeats()) {
            return ResponseEntity.badRequest().body("Too many seats selected");
        }

        List<Booking> existingBookings =
                bookingRepository.findByShowIdAndStatus(
                        show.getId(), "CONFIRMED");

        for (Booking existing : existingBookings) {
            String[] bookedSeats = existing.getSeats().split(",");

            for (String selected : selectedSeats) {
                for (String booked : bookedSeats) {
                    if (selected.trim().equalsIgnoreCase(booked.trim())) {
                        return ResponseEntity.badRequest()
                                .body("Seat already booked: " + selected.trim());
                    }
                }
            }
        }

        booking.setId(null);
        booking.setUserId(user.getId());
        Movie movie = movieRepository.findById(show.getMovieId())
        .orElse(null);

if (movie != null) {
    booking.setMovieTitle(movie.getTitle());
}
        booking.setTotalPrice(calculateTotalPrice(selectedSeats, show.getTicketPrice()));
        booking.setStatus("CONFIRMED");
        booking.setBookingTime(LocalDateTime.now());

        return ResponseEntity.ok(bookingRepository.save(booking));
    }

    private double calculateTotalPrice(String[] seats, double platinumPrice) {

    double total = 0;

    for (String seat : seats) {

        char row = Character.toUpperCase(
                seat.trim().charAt(0)
        );

        if (row >= 'A' && row <= 'N') {
            total += platinumPrice;

        } else if (row >= 'O' && row <= 'V') {
            total += platinumPrice * 0.75;

        } else if (row == 'W') {
            total += platinumPrice * 0.50;
        }
    }

    return Math.round(total * 100.0) / 100.0;
}

    @GetMapping("/my")
    public List<BookingResponse> getMyBookings(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        List<Booking> bookings =
                bookingRepository.findByUserIdOrderByIdDesc(user.getId());

        return bookings.stream().map(booking -> {

            MovieShow show = showRepository.findById(booking.getShowId())
                    .orElse(null);

            String movieTitle = booking.getMovieTitle() != null
        ? booking.getMovieTitle()
        : "Movie details unavailable";
            LocalDate showDate = null;
            LocalTime showTime = null;

            if (show != null) {
                showDate = show.getShowDate();
                showTime = show.getShowTime();

                Movie movie = movieRepository.findById(show.getMovieId())
                        .orElse(null);

                if (movie != null) {
                    movieTitle = movie.getTitle();
                }
            }

            return new BookingResponse(
                    booking.getId(),
                    booking.getShowId(),
                    movieTitle,
                    showDate,
                    showTime,
                    booking.getSeats(),
                    booking.getTotalPrice(),
                    booking.getStatus(),
                    booking.getBookingTime()
            );

        }).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Booking booking = bookingRepository.findById(id)
                .orElse(null);

        if (booking == null) {
            return ResponseEntity.notFound().build();
        }

        if (!booking.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body("You cannot cancel another user's booking");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        return ResponseEntity.ok("Booking cancelled");
    }

    public record BookingResponse(
            Long id,
            Long showId,
            String movieTitle,
            LocalDate showDate,
            LocalTime showTime,
            String seats,
            double totalPrice,
            String status,
            LocalDateTime bookingTime
    ) {}
}
