package com.dentalClinic.dental.service.impl;

import com.dentalClinic.dental.dto.input.appointment.AppointmentInputDto;
import com.dentalClinic.dental.dto.output.appointment.AppointmentOutputDto;
import com.dentalClinic.dental.dto.update.AppointmentUpdateInputDto;
import com.dentalClinic.dental.entity.Appointment;
import com.dentalClinic.dental.repository.AppointmentRepository;
import com.dentalClinic.dental.repository.DentistRepository;
import com.dentalClinic.dental.service.IAppointmentService;
import com.dentalClinic.dental.utils.JsonPrinter;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AppointmentService implements IAppointmentService {
    private final Logger LOGGER =
            LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final PatientService patientService;
    private final DentistService dentistService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ModelMapper modelMapper,
                              PatientService patientService,
                              DentistService dentistService) {
        this.appointmentRepository = appointmentRepository;
        this.modelMapper = modelMapper;
        this.patientService = patientService;
        this.dentistService = dentistService;
    }

    /*
        registerAppointment: This method creates a new appointment record in the
        database.
        Parameters: appointment: An instance of AppointmentInputDto containing
        the appointment information.
        Return Value: An instance of AppointmentOutputDto containing the created
        appointment information.
        Usage example:
        AppointmentInputDto appointmentInputDto = new AppointmentInputDto();
        appointmentInputDto.setDentistId(12345L);
        appointmentInputDto.setDate("2023-11-24");
        appointmentInputDto.setTime("10:00");
        appointmentInputDto.setPatientName("John Doe");

        AppointmentOutputDto appointmentOutputDto = dentistService
        .registerAppointment(appointmentInputDto);

        if (appointmentOutputDto != null) {
            System.out.println("Appointment registered successfully");
        } else {
            System.out.println("Appointment registration failed");
        }
    */
    @Override
    public AppointmentOutputDto registerAppointment(AppointmentInputDto appointment) {
        LOGGER.info("AppointmentInputDto: " + JsonPrinter.toString(appointment));
        Appointment appointmentEntity = modelMapper.map(appointment,
                Appointment.class);

        Appointment appointmentToPersist =
                appointmentRepository.save(appointmentEntity);
        AppointmentOutputDto appointmentOutputDto =
                modelMapper.map(appointmentToPersist,
                        AppointmentOutputDto.class);
        LOGGER.info("AppointmentOutputDto : " + JsonPrinter.toString(appointmentOutputDto));
        return appointmentOutputDto;
    }

    /*
    listAppointments: This method retrieves all appointment records from the
    database;
    Parameters: None;
    Return Value: A list of AppointmentOutputDto instances, each containing
    the information of an appointment;
    Usage Example:
    List<AppointmentOutputDto> appointmentsOutputDto = dentistService
    .listAppointments();
    System.out.println("List of appointments:");
    for (AppointmentOutputDto appointmentOutputDto : appointmentsOutputDto) {
    System.out.println(appointmentOutputDto);
    }
*/
    @Override
    public List<AppointmentOutputDto> listAppointments() {

        List<AppointmentOutputDto> appointmentsOutputDto =
                appointmentRepository.findAll()
                        .stream()
                        .map(appointment -> modelMapper.map(appointment,
                                AppointmentOutputDto.class))
                        .toList();

        if (LOGGER.isInfoEnabled())
            LOGGER.info("List of all appointments: {}",
                    JsonPrinter.toString(appointmentsOutputDto));
        return appointmentsOutputDto;
    }

    @Override
    public AppointmentOutputDto searchAppointmentForId(Long id) {

        Appointment searchedAppointment =
                appointmentRepository.findBy(id).orElse(null);
        AppointmentOutputDto appointmentFound = null;

        if (searchedAppointment != null) {
            appointmentFound = modelMapper.map(searchedAppointment,
                    AppointmentOutputDto.class);
            LOGGER.info("Appointment found: {}",
                    JsonPrinter.toString(appointmentFound));
        } else LOGGER.error("The id is not registered in the database");
        return appointmentFound;
    }

    @Override
    public AppointmentOutputDto updateAppointment(AppointmentUpdateInputDto appointment) throws ConfigDataResourceNotFoundException {

        Appointment appointmentReceived = modelMapper.map(appointment,
                Appointment.class);
        Appointment appointmentToUpdate =
                appointmentRepository.findById(appointmentReceived.getId()).orElse(null);
        AppointmentOutputDto appointmentOutputDto = null;
        if(appointmentToUpdate != null){
            appointmentToUpdate = appointmentReceived;
            appointmentRepository.save(appointmentToUpdate);

            appointmentOutputDto = modelMapper.map(appointmentToUpdate,
                    AppointmentOutputDto.class);
            LOGGER.warn("Updated appointment: {}", JsonPrinter.toString(appointmentOutputDto));
        }


        return null;
    }

    @Override
    public void deleteAppointment(Long id) {

    }
}
