package volodea.gymbookingsystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import volodea.gymbookingsystem.dto.BookingRequest;
import volodea.gymbookingsystem.dto.BookingResponse;
import volodea.gymbookingsystem.entity.Booking;
import volodea.gymbookingsystem.entity.BookingStatus;
import volodea.gymbookingsystem.exception.InvalidBookingStateException;
import volodea.gymbookingsystem.exception.NoAvailableSpotsException;
import volodea.gymbookingsystem.service.BookingService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Test
    void shouldReturnCreatedBookingResponse() throws Exception {
        BookingResponse bookingResponse = new BookingResponse(1L, "Main"
                , LocalDateTime.now(), BookingStatus.PENDING, LocalDateTime.now());

        when(bookingService.createBooking(any(BookingRequest.class), eq(1L))).thenReturn(bookingResponse);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", "1")
                .content("""
                        {
                        
                            "gymClassId": 10
                            
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.gymClassTitle").value("Main"));
    }

    @Test
    void shouldReturn409WhenNoAvailableSpots() throws Exception {
        when(bookingService.createBooking(any(BookingRequest.class), eq(1L)))
                .thenThrow(new NoAvailableSpotsException(10L));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "gymClassId": 10
                            }    
                        """)
                .param("userId", "1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void should400WhenGymClassIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WhenApprovedBooking() throws Exception {
        when(bookingService.approveBooking(1L)).thenReturn(new BookingResponse(1L
                , "Main", LocalDateTime.now(), BookingStatus.CONFIRMED, LocalDateTime.now()));

        mockMvc.perform(patch("/api/bookings/{bookingId}/approve", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    void shouldReturn409WhenApprovingNonPendingBooking() throws Exception {
        when(bookingService.approveBooking(1L)).thenThrow(new InvalidBookingStateException(1L));

        mockMvc.perform(patch("/api/bookings/{bookingId}/approve", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturn200WhenRejectedBooking() throws Exception {
        when(bookingService.rejectBooking(1L)).thenReturn(new BookingResponse(1L
                , "Main", LocalDateTime.now(), BookingStatus.REJECTED, LocalDateTime.now()));

        mockMvc.perform(patch("/api/bookings/{bookingId}/reject", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.bookingStatus").value("REJECTED"));
    }

    @Test
    void shouldReturnListOfPendingBookings() throws Exception {
        List<BookingResponse> bookings = new ArrayList<>();
        bookings.add(new BookingResponse(1L
                , "Main", LocalDateTime.now(), BookingStatus.PENDING, LocalDateTime.now()));

        when(bookingService.getPendingBookings()).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$") .isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].bookingStatus").value("PENDING"));
    }

    @Test
    void shouldReturnEmptyListWhenNoBookings() throws Exception {
        when(bookingService.getPendingBookings()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/bookings/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$") .isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnBookingForUser() throws Exception {
        List<BookingResponse> bookings = new ArrayList<>();
        bookings.add(new BookingResponse(1L
                , "Main", LocalDateTime.now(), BookingStatus.PENDING, LocalDateTime.now()));

        when(bookingService.getBookingsByUserId(1L)).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/my")
                .param("userId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1L));
    }
}
