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
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "Bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping (value = "ForAdmin")
    public Page<BookingDto> getAllBookingsForAdmin(@RequestParam Long idConsortium, Pageable page) {
        return bookingService.getAllBookingsForAdmin(idConsortium, page);
    }

    @GetMapping (value = "ForResident")
    public Page<BookingDto> getBookingsForResident(@RequestParam Long idConsortium, Long idResident, Pageable page) {
        return bookingService.getBookingsForResident(idConsortium,idResident, page);
    }

    @GetMapping (value = "available-dates")
    public List<DateShiftDto> getAvailableDates(@RequestParam Long idAmenity) throws EntityNotFoundException {
        return bookingService.getAvailableDates(idAmenity);
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto) throws BookingNotAvailableException, EntityNotFoundException, BookingLimitExceededException {
        return bookingService.createBooking(bookingDto);
    }

    @DeleteMapping (value = "{idBooking}")
    public void deleteBooking(@PathVariable Long idBooking) throws EntityNotFoundException {
        bookingService.deleteBooking(idBooking);
    }

}
