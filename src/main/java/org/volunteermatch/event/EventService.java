package org.volunteermatch.event;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.volunteermatch.common.ApiException;
import org.volunteermatch.registration.RegistrationRepository;
import org.volunteermatch.registration.RegistrationDtos;
import org.volunteermatch.registration.RegistrationStatus;
import org.volunteermatch.registration.VolunteerRegistration;
import org.volunteermatch.user.AppUser;
import org.volunteermatch.user.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class EventService {
    private final EventRepository events;
    private final RegistrationRepository registrations;

    public EventService(EventRepository events, RegistrationRepository registrations) {
        this.events = events;
        this.registrations = registrations;
    }

    @Transactional(readOnly = true)
    public List<EventDtos.EventResponse> list(String query, String city, String category, AppUser viewer) {
        String normalizedQuery = normalize(query);
        String normalizedCity = normalize(city);
        String normalizedCategory = normalize(category);
        return events.findAllByOrderByStartsAtAsc().stream()
                .filter(event -> !event.getStartsAt().isBefore(LocalDateTime.now()))
                .filter(event -> normalizedCity == null || event.getCity().toLowerCase(Locale.ROOT).contains(normalizedCity))
                .filter(event -> normalizedCategory == null || event.getCategory().equalsIgnoreCase(normalizedCategory))
                .filter(event -> normalizedQuery == null
                        || (event.getTitle() + " " + event.getDescription() + " " + event.getCategory()
                        + " " + event.getCity() + " " + event.getLocation()).toLowerCase(Locale.ROOT).contains(normalizedQuery))
                .map(event -> response(event, viewer))
                .toList();
    }

    @Transactional(readOnly = true)
    public EventDtos.EventResponse get(Long id, AppUser viewer) {
        return response(find(id), viewer);
    }

    @Transactional
    public EventDtos.EventResponse create(EventDtos.EventRequest request, AppUser organizer) {
        requireOrganizer(organizer);
        VolunteerEvent event = events.save(new VolunteerEvent(request.title().trim(), request.description().trim(),
                request.category().trim(), request.location().trim(), request.city().trim(),
                request.startsAt(), request.durationHours(), request.capacity(), clean(request.imageUrl()), organizer));
        return response(event, organizer);
    }

    @Transactional
    public EventDtos.EventResponse update(Long id, EventDtos.EventRequest request, AppUser organizer) {
        VolunteerEvent event = find(id);
        requireOwner(event, organizer);
        event.update(request.title().trim(), request.description().trim(), request.category().trim(),
                request.location().trim(), request.city().trim(), request.startsAt(), request.durationHours(),
                request.capacity(), clean(request.imageUrl()));
        return response(event, organizer);
    }

    @Transactional
    public void delete(Long id, AppUser organizer) {
        VolunteerEvent event = find(id);
        requireOwner(event, organizer);
        registrations.deleteByEventId(id);
        events.delete(event);
    }

    @Transactional(readOnly = true)
    public List<EventDtos.EventResponse> organizerEvents(AppUser organizer) {
        requireOrganizer(organizer);
        return events.findByOrganizerIdOrderByStartsAtAsc(organizer.getId()).stream()
                .map(event -> response(event, organizer)).toList();
    }

    @Transactional(readOnly = true)
    public List<RegistrationDtos.RegistrationResponse> participants(Long id, AppUser organizer) {
        VolunteerEvent event = find(id);
        requireOwner(event, organizer);
        EventDtos.EventResponse eventResponse = response(event, organizer);
        return registrations.findByEventIdOrderByRegisteredAtAsc(id).stream()
                .filter(registration -> registration.getStatus() != RegistrationStatus.CANCELLED)
                .map(registration -> RegistrationDtos.RegistrationResponse.from(registration, eventResponse))
                .toList();
    }

    private EventDtos.EventResponse response(VolunteerEvent event, AppUser viewer) {
        RegistrationStatus status = viewer == null || viewer.getRole() != Role.VOLUNTEER ? null
                : registrations.findByEventIdAndVolunteerId(event.getId(), viewer.getId())
                .map(VolunteerRegistration::getStatus).orElse(null);
        long count = registrations.countByEventIdAndStatusIn(event.getId(),
                List.of(RegistrationStatus.REGISTERED, RegistrationStatus.ATTENDED));
        return EventDtos.EventResponse.from(event, count, status);
    }

    private VolunteerEvent find(Long id) {
        return events.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found."));
    }

    private void requireOwner(VolunteerEvent event, AppUser user) {
        requireOrganizer(user);
        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only manage events posted by your organization.");
        }
    }

    private void requireOrganizer(AppUser user) {
        if (user.getRole() != Role.ORGANIZER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "An organizer account is required for this action.");
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
