package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.BookingDto;
import com.utn.interactiveconsortium.dto.DateShiftDto;
import com.utn.interactiveconsortium.exception.BookingLimitExceededException;
import com.utn.interactiveconsortium.exception.BookingNotAvailableException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "Bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping (value = "consortium/{idConsortium}/ForAdmin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Page<BookingDto> getAllBookingsForAdmin(@PathVariable Long idConsortium, Pageable page) {
        return bookingService.getAllBookingsForAdmin(idConsortium, page);
    }

    @GetMapping (value = "/consortium/{idConsortium}/ForResident")
    @PreAuthorize("hasAnyAuthority('ROLE_RESIDENT')")
    public Page<BookingDto> getBookingsForResident(@PathVariable Long idConsortium, Pageable page) {
        return bookingService.getBookingsForResident(idConsortium, page);
    }

    @GetMapping (value = "available-dates")
    public List<DateShiftDto> getAvailableDates(@RequestParam Long idAmenity) throws EntityNotFoundException {
        return bookingService.getAvailableDates(idAmenity);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RESIDENT')")
    public BookingDto createBooking(@RequestBody BookingDto bookingDto) throws BookingNotAvailableException, EntityNotFoundException, BookingLimitExceededException {
        return bookingService.createBooking(bookingDto);
    }

    @DeleteMapping (value = "/{idBooking}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESIDENT')")
    public void deleteBooking(@PathVariable Long idBooking) throws EntityNotFoundException {
        bookingService.deleteBooking(idBooking);
    }

}
