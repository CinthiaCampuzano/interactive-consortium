package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.AmenitiesBookingAvailableDto;
import com.utn.interactiveconsortium.dto.BookingDto;
import com.utn.interactiveconsortium.dto.DateShiftDto;
import com.utn.interactiveconsortium.entity.AmenityEntity;
import com.utn.interactiveconsortium.entity.BookingEntity;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.enums.EBookingStatus;
import com.utn.interactiveconsortium.enums.EShift;
import com.utn.interactiveconsortium.exception.BookingLimitExceededException;
import com.utn.interactiveconsortium.exception.BookingNotAvailableException;
import com.utn.interactiveconsortium.exception.CustomGenericException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.BookingMapper;
import com.utn.interactiveconsortium.repository.AmenityRepository;
import com.utn.interactiveconsortium.repository.BookingRepository;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final AmenityRepository amenityRepository;

    private final PersonRepository personRepository;

    private final LoggedUserService loggedUserService;

    private final ConsortiumRepository consortiumRepository;

    public Page<BookingDto> getAllBookingsForAdmin(Long idConsortium, Long amenityId, EShift shift, LocalDate date, String departmentCode, EBookingStatus status, Pageable page) {
        return bookingMapper.toPage(bookingRepository.findBookingsForAdminWithFilters(
              idConsortium,
              amenityId,
              shift,
              date,
              departmentCode,
              status,
              page
        ));
    }

    public Page<BookingDto> getBookingsForResident(Long idConsortium, Long amenityId, EShift shift, LocalDate date, String departmentCode, EBookingStatus status, Pageable page) {

        Long residentId = loggedUserService.getLoggedPerson().getPersonId();
        return bookingMapper.toPage(
              bookingRepository.findBookingsForResidentWithFilters(idConsortium, residentId,
                    amenityId,
                    shift,
                    date,
                    departmentCode,
                    status,
                    page
              ));

    }

    public List<DateShiftDto> getAvailableDates(Long idAmenity) throws EntityNotFoundException {

        boolean existsAmenity = amenityRepository.existsById(idAmenity);

        if (!existsAmenity) {
            throw new EntityNotFoundException("No existe ese espacio comun");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);

        List<DateShiftDto> allDatesShifts = new ArrayList<>();
        allDatesShifts.addAll(generateAllDatesShiftsInRange(startDate, endDate, EShift.MORNING));
        allDatesShifts.addAll(generateAllDatesShiftsInRange(startDate, endDate, EShift.NIGHT));

        List<BookingEntity> bookings = bookingRepository.findByAmenity_AmenityIdAndBookingStatus(idAmenity, EBookingStatus.PENDING);
        List<DateShiftDto> bookedDatesShifts = bookings
              .stream()
              .map(booking -> DateShiftDto.builder().date(booking.getStartDate()).shift(booking.getShift()).build())
              .toList();

        allDatesShifts.removeAll(bookedDatesShifts);

        return allDatesShifts;
    }

    public List<DateShiftDto> generateAllDatesShiftsInRange(LocalDate startDate, LocalDate endDate, EShift shift) {
        List<DateShiftDto> datesShiftsInRange = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            datesShiftsInRange.add(DateShiftDto.builder().date(date).shift(shift).build());
        }
        return datesShiftsInRange;
    }

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto)
          throws BookingNotAvailableException, EntityNotFoundException, BookingLimitExceededException {

        if (!bookingDto.getStartDate().isAfter(LocalDate.now())) {
            throw new BookingNotAvailableException("La fecha de la reserva debe ser posterior a la fecha actual");
        }

        AmenityEntity amenity = amenityRepository
              .findById(bookingDto.getAmenity().getAmenityId())
              .orElseThrow(() -> new EntityNotFoundException("No existe ese espacio comun"));

        if (!amenity.isActive()) {
            throw new BookingNotAvailableException("El espacio no esta habilitado en este momento.");
        }

        PersonEntity loggedPerson = loggedUserService.getLoggedPerson();

        DepartmentEntity department = amenity
              .getConsortium()
              .getDepartments()
              .stream()
              .filter(aDepartment -> aDepartment.getDepartmentId().equals(bookingDto.getDepartment().getDepartmentId()))
              .filter(aDepartment -> aDepartment.getResident().getPersonId().equals(loggedPerson.getPersonId()))
              .findFirst()
              .orElseThrow(() -> new EntityNotFoundException("El departamento no pertenece al residente"));

        if (!department.getActive()) {
            throw new EntityNotFoundException("El departamento no está activo");
        }

        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<BookingEntity> bookingsThisMonth = bookingRepository.findByDepartmentIdAndAmenityIdAndStartDateBetween(department.getDepartmentId(),
              amenity.getAmenityId(), firstDayOfMonth, lastDayOfMonth);

        if (bookingsThisMonth.size() >= amenity.getMaxBookings()) {
            throw new BookingLimitExceededException(
                  "El residente ya ha hecho " + amenity.getMaxBookings() + " reservas este mes para este espacio común");
        }

        List<DateShiftDto> dateShiftDtoList = getAvailableDates(bookingDto.getAmenity().getAmenityId());
        boolean isAvailable = dateShiftDtoList
              .stream()
              .anyMatch(dateShiftDto -> dateShiftDto.getDate().equals(bookingDto.getStartDate()) && dateShiftDto
                    .getShift()
                    .equals(bookingDto.getShift()));

        if (!isAvailable) {
            throw new BookingNotAvailableException("La fecha y turno seleccionados no están disponibles");
        }

        BookingEntity bookingEntity = bookingMapper.convertDtoToEntity(bookingDto);

        bookingEntity.setAmenity(amenity);
//        bookingEntity.setResident(loggedPerson);
        bookingEntity.setDepartment(department);
        bookingEntity.setBookingCost(amenity.getCostOfUse());
        bookingEntity.setBookingStatus(EBookingStatus.PENDING);

        bookingRepository.save(bookingEntity);

        BookingDto bookingDtoNew = bookingMapper.convertEntityToDto(bookingEntity);

        return bookingDtoNew;

    }

    public void deleteBooking(Long idBooking) throws EntityNotFoundException {

        BookingEntity bookingEntity = bookingRepository.findById(idBooking).orElseThrow(() -> new EntityNotFoundException("No existe esa reserva"));

        //        LocalDate now = LocalDate.now();
        //
        //        if (bookingEntity.getStartDate().equals(now)) {
        //            throw new EntityNotFoundException("No se puede eliminar una reserva en el día de su realización. " +
        //                    "Las reservas solo pueden eliminarse con más de 24 horas de anticipación.");
        //        }

        bookingRepository.deleteById(idBooking);
    }

    public List<AmenitiesBookingAvailableDto> amenitiesBookingAvailableList(Long consortiumId) throws EntityNotFoundException {

        PersonEntity loggedPerson = loggedUserService.getLoggedPerson();
        ConsortiumEntity consortium = consortiumRepository.findById(consortiumId)
              .orElseThrow(() ->new EntityNotFoundException("No existe el consorcio"));

        Map<Long, AmenityEntity> amenities = consortium
              .getAmenities()
              .stream()
              .collect(Collectors.toMap(AmenityEntity::getAmenityId, amenityEntity -> amenityEntity));

        List<DepartmentEntity> departments = consortium
              .getDepartments()
              .stream()
              .filter(departmentEntity -> departmentEntity.getResident() != null)
              .filter(departmentEntity -> departmentEntity.getResident().getPersonId().equals(loggedPerson.getPersonId()))
              .toList();

        Map<Long, Map<Long, Long>> bookingsForCurrentMonth = bookingRepository.findActiveBookingsFor(departments)
              .stream()
              .collect(Collectors.groupingBy(bookingEntity -> bookingEntity.getDepartment().getDepartmentId(),
                    Collectors.groupingBy(bookingEntity -> bookingEntity.getAmenity().getAmenityId(), Collectors.counting())));


        List<AmenitiesBookingAvailableDto> bookingAvailableList = new ArrayList<>();

        for (DepartmentEntity department : departments) {
            Long departmentId = department.getDepartmentId();
            for (Map.Entry<Long, AmenityEntity> amenity : amenities.entrySet()) {
                Long amenityId = amenity.getKey();
                AmenityEntity auxAmenity = amenity.getValue();
                int usedLimit = 0;
                int amenityBookingLimit = auxAmenity.getMaxBookings();

                if (bookingsForCurrentMonth.containsKey(departmentId)) {
                    Map<Long, Long> auxAmenities = bookingsForCurrentMonth.get(departmentId);
                    if (auxAmenities.containsKey(amenityId)) {
                        usedLimit = auxAmenities.get(amenityId).intValue();
                    }
                }
                AmenitiesBookingAvailableDto bookingAvailableDto = AmenitiesBookingAvailableDto
                      .builder()
                      .consortiumId(consortiumId)
                      .amenityId(amenityId)
                      .departmentId(departmentId)
                      .amenityBooking(usedLimit)
                      .amenityMaxBooking(amenityBookingLimit)
                      .build();
                bookingAvailableList.add(bookingAvailableDto);
            }
        }

        return bookingAvailableList;
    }

    //El proceso de actualizacion de reservas tiene que correr antes de la fecha de reserva.
    //Para las de turno manana corre a las 7 y actualiza su estado
    //Para las de turno tarde corre a las 16 y acualiza su estado
//    @Scheduled(cron = "0 0 7,16 * * *")
    @Scheduled(cron = "0 0/1 * * * *")
    @Transactional(rollbackFor = Exception.class)
    public void processBookings() {
        log.info("Proceso de actualizacion de de reservas INICIADO");
        int updatedBookings = processBookings(LocalDateTime.now());
        log.info("Proceso de actualizacion de de reservas FINALIZADO. Se actualizaron {} reservas", updatedBookings);
    }

    @Transactional(rollbackFor = Exception.class)
    public int processBookings(LocalDateTime dateTime) {
        // If dateTime is not provided, use current date and time
        LocalDateTime processDateTime = dateTime != null ? dateTime : LocalDateTime.now();
        LocalDate processDate = processDateTime.toLocalDate();

        // Get hour from the provided dateTime to determine which shifts to process
        int currentHour = processDateTime.getHour();

        // Get all bookings with PENDING status and start date up to the process date
        List<BookingEntity> pendingBookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getBookingStatus() == EBookingStatus.PENDING)
                .filter(booking -> !booking.getStartDate().isAfter(processDate))
                .collect(Collectors.toList());

        // Filter bookings by shift based on execution time
        List<BookingEntity> bookingsToProcess;
        if (currentHour < 15) {
            // At 15:00, only process MORNING bookings
            bookingsToProcess = pendingBookings.stream()
                    .filter(booking -> booking.getShift() == EShift.MORNING)
                    .collect(Collectors.toList());
        } else {
            // At night, process both MORNING and NIGHT bookings
            bookingsToProcess = pendingBookings;
        }

        // Update booking status based on amenity active status
        LocalDate period = LocalDate.now().withDayOfMonth(1);
        for (BookingEntity booking : bookingsToProcess) {
            AmenityEntity amenity = booking.getAmenity();
            if (amenity.isActive()) {
                booking.setBookingStatus(EBookingStatus.DONE);
                booking.setPeriod(period);
            } else {
                booking.setBookingStatus(EBookingStatus.AUTOMATIC_CANCELLED);
            }
        }

        // Save all updated bookings
        if (!bookingsToProcess.isEmpty()) {
            bookingRepository.saveAll(bookingsToProcess);
        }

        return bookingsToProcess.size();
    }

    public BookingDto updateForAdmin(BookingDto bookingDto) throws EntityNotFoundException {
        BookingEntity bookingToUpdate = bookingRepository
              .findById(bookingDto.getBookingId())
              .orElseThrow(() -> new EntityNotFoundException("La reserva no existe"));

        bookingToUpdate.setBookingStatus(bookingDto.getBookingStatus());
        bookingToUpdate.setStartDate(bookingDto.getStartDate());
        return bookingMapper.convertEntityToDto(bookingRepository.save(bookingToUpdate));
    }

    public BookingDto cancelBookingById(Long bookingId) throws CustomGenericException, EntityNotFoundException {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("No existe esa reserva"));

        LocalDate now = LocalDate.now();

        if (bookingEntity.getStartDate().equals(now)) {
            throw new CustomGenericException("No se puede eliminar una reserva en el día de su realización. "
                  + "Las reservas solo pueden eliminarse con más de 24 horas de anticipación.");
        }

        bookingEntity.setBookingStatus(EBookingStatus.USER_CANCELLED);
        return bookingMapper.convertEntityToDto(bookingRepository.save(bookingEntity));
    }

    public BigDecimal getBookingTotalAmountFor(ConsortiumEntity consortium, LocalDate period) {
        List<BookingEntity> bookings = bookingRepository.findAllBy(consortium.getConsortiumId(), null, EBookingStatus.DONE, period);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BookingEntity booking : bookings) {
            totalAmount = totalAmount.add(booking.getBookingCost());
        }
        return totalAmount;
    }

    public BigDecimal getDepartmentBookingCostForPeriod(DepartmentEntity department, LocalDate period) {
        List<BookingEntity> bookings = bookingRepository.findAllBy(
              department.getConsortium().getConsortiumId(),
              department.getDepartmentId(),
              EBookingStatus.DONE,
              period
        );
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BookingEntity booking : bookings) {
            totalAmount = totalAmount.add(booking.getBookingCost());
        }
        return totalAmount;
    }
}
