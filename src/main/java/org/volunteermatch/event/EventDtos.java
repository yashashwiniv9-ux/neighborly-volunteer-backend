package org.volunteermatch.event;

import jakarta.validation.constraints.*;
import org.volunteermatch.registration.RegistrationStatus;
import org.volunteermatch.user.AppUser;

import java.time.LocalDateTime;

public final class EventDtos {
    private EventDtos() {}

    public record EventRequest(
            @NotBlank @Size(max = 160) String title,
            @NotBlank @Size(max = 3000) String description,
            @NotBlank @Size(max = 60) String category,
            @NotBlank @Size(max = 180) String location,
            @NotBlank @Size(max = 120) String city,
            @NotNull @Future LocalDateTime startsAt,
            @Min(1) @Max(24) int durationHours,
            @Min(1) @Max(10000) int capacity,
            @Size(max = 300) String imageUrl) {}

    public record EventResponse(Long id, String title, String description, String category, String location,
                                String city, LocalDateTime startsAt, int durationHours, int capacity,
                                String imageUrl, Long organizerId, String organizerName, String organizerOrganization,
                                long registeredCount, boolean registered, RegistrationStatus registrationStatus) {
        public static EventResponse from(VolunteerEvent event, long registeredCount, RegistrationStatus status) {
            AppUser organizer = event.getOrganizer();
            return new EventResponse(event.getId(), event.getTitle(), event.getDescription(), event.getCategory(),
                    event.getLocation(), event.getCity(), event.getStartsAt(), event.getDurationHours(),
                    event.getCapacity(), event.getImageUrl(), organizer.getId(), organizer.getName(),
                    organizer.getOrganizationName(), registeredCount, status == RegistrationStatus.REGISTERED, status);
        }
    }

    public record AttendanceRequest(@Min(0) @Max(24) int hours) {}
}
