package volodea.gymbookingsystem.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import volodea.gymbookingsystem.dto.BookingRequest;
import volodea.gymbookingsystem.dto.BookingResponse;
import volodea.gymbookingsystem.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestParam Long userId
            , @Valid @RequestBody BookingRequest bookingRequest) {

        BookingResponse response = bookingService.createBooking(bookingRequest, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{bookingId}/approve")
    public BookingResponse approveBooking(@PathVariable Long bookingId) {
        return bookingService.approveBooking(bookingId);
    }

    @PostMapping("/{bookingId}/reject")
    public BookingResponse rejectBooking(@PathVariable Long bookingId) {
        return bookingService.rejectBooking(bookingId);
    }
}
