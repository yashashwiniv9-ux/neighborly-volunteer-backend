package org.volunteermatch.registration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.volunteermatch.common.ApiException;
import org.volunteermatch.event.EventDtos;
import org.volunteermatch.event.EventRepository;
import org.volunteermatch.event.VolunteerEvent;
import org.volunteermatch.user.AppUser;
import org.volunteermatch.user.Role;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {
    private final EventRepository events;
    private final RegistrationRepository registrations;

    public RegistrationService(EventRepository events, RegistrationRepository registrations) {
        this.events = events;
        this.registrations = registrations;
    }

    @Transactional
    public RegistrationDtos.RegistrationResponse register(Long eventId, AppUser volunteer) {
        requireVolunteer(volunteer);
        VolunteerEvent event = findEvent(eventId);
        if (!event.getStartsAt().isAfter(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.CONFLICT, "Registration is closed for this event.");
        }
        VolunteerRegistration registration = registrations.findByEventIdAndVolunteerId(eventId, volunteer.getId())
                .orElse(null);
        if (registration != null && registration.getStatus() != RegistrationStatus.CANCELLED) {
            throw new ApiException(HttpStatus.CONFLICT, "You are already registered for this event.");
        }
        long active = registrations.countByEventIdAndStatusIn(eventId,
                List.of(RegistrationStatus.REGISTERED, RegistrationStatus.ATTENDED));
        if (active >= event.getCapacity()) {
            throw new ApiException(HttpStatus.CONFLICT, "This event is full.");
        }
        if (registration == null) {
            registration = registrations.save(new VolunteerRegistration(event, volunteer));
        } else {
            registration.reRegister();
        }
        return RegistrationDtos.RegistrationResponse.from(registration,
                EventDtos.EventResponse.from(event, active + 1, RegistrationStatus.REGISTERED));
    }

    @Transactional
    public void cancel(Long eventId, AppUser volunteer) {
        requireVolunteer(volunteer);
        VolunteerRegistration registration = registrations.findByEventIdAndVolunteerId(eventId, volunteer.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Registration not found."));
        if (registration.getStatus() == RegistrationStatus.ATTENDED) {
            throw new ApiException(HttpStatus.CONFLICT, "Completed attendance cannot be cancelled.");
        }
        registration.cancel();
    }

    @Transactional(readOnly = true)
    public List<RegistrationDtos.RegistrationResponse> mine(AppUser volunteer) {
        requireVolunteer(volunteer);
        return registrations.findByVolunteerIdOrderByRegisteredAtDesc(volunteer.getId()).stream()
                .filter(registration -> registration.getStatus() != RegistrationStatus.CANCELLED)
                .map(registration -> {
                    VolunteerEvent event = registration.getEvent();
                    long count = registrations.countByEventIdAndStatusIn(event.getId(),
                            List.of(RegistrationStatus.REGISTERED, RegistrationStatus.ATTENDED));
                    return RegistrationDtos.RegistrationResponse.from(registration,
                            EventDtos.EventResponse.from(event, count, registration.getStatus()));
                }).toList();
    }

    @Transactional
    public void markAttendance(Long eventId, Long registrationId, int hours, AppUser organizer) {
        VolunteerEvent event = findEvent(eventId);
        if (event.getOrganizer().getId() == null || !event.getOrganizer().getId().equals(organizer.getId())
                || organizer.getRole() != Role.ORGANIZER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only manage registrations for your own events.");
        }
        VolunteerRegistration registration = registrations.findById(registrationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Registration not found."));
        if (!registration.getEvent().getId().equals(eventId)
                || registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Active registration not found for this event.");
        }
        registration.markAttended(hours);
    }

    private VolunteerEvent findEvent(Long id) {
        return events.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found."));
    }

    private void requireVolunteer(AppUser user) {
        if (user.getRole() != Role.VOLUNTEER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "A volunteer account is required for this action.");
        }
    }
}
