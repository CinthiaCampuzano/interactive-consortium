package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.BookingDto;
import com.utn.interactiveconsortium.dto.DateShiftDto;
import com.utn.interactiveconsortium.entity.AmenityEntity;
import com.utn.interactiveconsortium.entity.BookingEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.enums.Shift;
import com.utn.interactiveconsortium.exception.BookingLimitExceededException;
import com.utn.interactiveconsortium.exception.BookingNotAvailableException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.BookingMapper;
import com.utn.interactiveconsortium.repository.AmenityRepository;
import com.utn.interactiveconsortium.repository.BookingRepository;
import com.utn.interactiveconsortium.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final AmenityRepository amenityRepository;

    private final PersonRepository personRepository;

    private final LoggedUserService loggedUserService;

    public Page<BookingDto> getAllBookingsForAdmin(Long idConsortium, Pageable page) {
        return bookingMapper.toPage(bookingRepository.findByAmenity_Consortium_ConsortiumId(idConsortium, page));
    }

    public Page<BookingDto> getBookingsForResident(Long idConsortium, Pageable page) {

        Long residentId = loggedUserService.getLoggedPerson().getPersonId();
        return bookingMapper.toPage(bookingRepository.findByAmenity_Consortium_ConsortiumIdAndResident_PersonId(idConsortium, residentId, page));

    }

    public List<DateShiftDto> getAvailableDates(Long idAmenity) throws EntityNotFoundException {

        boolean existsAmeneity = amenityRepository.existsById(idAmenity);

        if (!existsAmeneity) {
            throw new EntityNotFoundException("No existe ese espacio comun");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);

        List<DateShiftDto> allDatesShifts = new ArrayList<>();
        allDatesShifts.addAll(generateAllDatesShiftsInRange(startDate, endDate, Shift.MORNING));
        allDatesShifts.addAll(generateAllDatesShiftsInRange(startDate, endDate, Shift.NIGHT));

        List<BookingEntity> bookings = bookingRepository.findByAmenity_AmenityId(idAmenity);
        List<DateShiftDto> bookedDatesShifts = bookings.stream()
                .map(booking -> DateShiftDto.builder()
                        .date(booking.getStartDate())
                        .shift(booking.getShift())
                        .build())
                .toList();

        allDatesShifts.removeAll(bookedDatesShifts);

        return allDatesShifts;
    }

    public List<DateShiftDto> generateAllDatesShiftsInRange(LocalDate startDate, LocalDate endDate, Shift shift) {
        List<DateShiftDto> datesShiftsInRange = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            datesShiftsInRange.add(DateShiftDto.builder()
                    .date(date)
                    .shift(shift)
                    .build());
        }
        return datesShiftsInRange;
    }

    public BookingDto createBooking(BookingDto bookingDto) throws BookingNotAvailableException, EntityNotFoundException, BookingLimitExceededException {

        AmenityEntity amenity = amenityRepository.findById(bookingDto.getAmenity().getAmenityId())
                .orElseThrow(() -> new EntityNotFoundException("No existe ese espacio comun"));

        PersonEntity resident = loggedUserService.getLoggedPerson();

        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<BookingEntity> bookingsThisMonth = bookingRepository.findByResidentIdAndAmenityIdAndStartDateBetween(resident.getPersonId(), amenity.getAmenityId(), firstDayOfMonth, lastDayOfMonth);

        if (bookingsThisMonth.size() >= amenity.getMaxBookings()) {
            throw new BookingLimitExceededException("El residente ya ha hecho " + (amenity.getMaxBookings() + 1) + " reservas este mes para este espacio común");
        }

        List<DateShiftDto> dateShiftDtoList = getAvailableDates(bookingDto.getAmenity().getAmenityId());
        boolean isAvailable = dateShiftDtoList.stream()
                .anyMatch(dateShiftDto -> dateShiftDto.getDate().equals(bookingDto.getStartDate()) && dateShiftDto.getShift().equals(bookingDto.getShift()));

        if (!isAvailable) {
            throw new BookingNotAvailableException("La fecha y turno seleccionados no están disponibles");
        }

        BookingEntity bookingEntity = bookingMapper.convertDtoToEntity(bookingDto);

        bookingEntity.setAmenity(amenity);
        bookingEntity.setResident(resident);

        bookingRepository.save(bookingEntity);

        BookingDto bookingDtoNew = bookingMapper.convertEntityToDto(bookingEntity);

        return bookingDtoNew;

    }

    public void deleteBooking(Long idBooking) throws EntityNotFoundException {

        BookingEntity bookingEntity = bookingRepository.findById(idBooking)
                .orElseThrow(() -> new EntityNotFoundException("No existe esa reserva"));

        LocalDate now = LocalDate.now();

        if (bookingEntity.getStartDate().equals(now)) {
            throw new EntityNotFoundException("No se puede eliminar una reserva en el día de su realización. " +
                    "Las reservas solo pueden eliminarse con más de 24 horas de anticipación.");
        }
        bookingRepository.deleteById(idBooking);
    }

}
