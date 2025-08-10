package com.utn.interactiveconsortium.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.dto.AmenitiesBookingAvailableDto;
import com.utn.interactiveconsortium.dto.BookingDto;
import com.utn.interactiveconsortium.dto.DateShiftDto;
import com.utn.interactiveconsortium.enums.EBookingStatus;
import com.utn.interactiveconsortium.enums.EShift;
import com.utn.interactiveconsortium.exception.BookingLimitExceededException;
import com.utn.interactiveconsortium.exception.BookingNotAvailableException;
import com.utn.interactiveconsortium.exception.CustomGenericException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "Bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping (value = "consortium/{idConsortium}/ForAdmin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Page<BookingDto> getAllBookingsForAdmin(@PathVariable Long idConsortium,
          @RequestParam(required = false) Long amenityId,
          @RequestParam(required = false) EShift shift,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
          @RequestParam(required = false) String departmentCode,
          @RequestParam(required = false) EBookingStatus status,
          Pageable page) {
        return bookingService.getAllBookingsForAdmin(idConsortium, amenityId, shift, date, departmentCode, status, page);
    }

    @GetMapping (value = "/consortium/{idConsortium}/ForResident")
    @PreAuthorize("hasAnyAuthority('ROLE_RESIDENT')")
    public Page<BookingDto> getBookingsForResident(@PathVariable Long idConsortium,
          @RequestParam(required = false) Long amenityId,
          @RequestParam(required = false) EShift shift,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
          @RequestParam(required = false) String departmentCode,
          @RequestParam(required = false) EBookingStatus status,
          Pageable page) {
        return bookingService.getBookingsForResident(idConsortium, amenityId, shift, date, departmentCode, status, page);
    }

    @GetMapping (value = "available-dates")
    public List<DateShiftDto> getAvailableDates(@RequestParam Long idAmenity) throws EntityNotFoundException {
        return bookingService.getAvailableDates(idAmenity);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RESIDENT')")
    public BookingDto createBooking(@RequestBody @Valid BookingDto bookingDto) throws BookingNotAvailableException, EntityNotFoundException, BookingLimitExceededException {
        return bookingService.createBooking(bookingDto);
    }

//    @DeleteMapping (value = "/{idBooking}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESIDENT')")
//    public void deleteBooking(@PathVariable Long idBooking) throws EntityNotFoundException {
//        bookingService.deleteBooking(idBooking);
//    }

    @GetMapping(value = "/consortium/{consortiumId}/available-bookings")
    @PreAuthorize("hasAnyAuthority('ROLE_RESIDENT')")
    public List<AmenitiesBookingAvailableDto> amenitiesBookingAvailableList(@PathVariable Long consortiumId) throws EntityNotFoundException {
        return bookingService.amenitiesBookingAvailableList(consortiumId);
    }

    @PutMapping(value = "/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public BookingDto updateForAdmin(@PathVariable Long bookingId, @RequestBody @Valid BookingDto bookingDto) throws EntityNotFoundException {
        return bookingService.updateForAdmin(bookingDto);
    }

    @PutMapping(value = "/{bookingId}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_RESIDENT')")
    public BookingDto cancelBookingById(@PathVariable Long bookingId) throws EntityNotFoundException, CustomGenericException {
        return bookingService.cancelBookingById(bookingId);
    }

}
