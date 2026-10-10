package org.volunteermatch.registration;

import org.volunteermatch.event.EventDtos;
import org.volunteermatch.user.UserDtos;

import java.time.Instant;

public final class RegistrationDtos {
    private RegistrationDtos() {}

    public record RegistrationResponse(Long id, EventDtos.EventResponse event, UserDtos.UserResponse volunteer,
                                       RegistrationStatus status, int hoursLogged, Instant registeredAt) {
        public static RegistrationResponse from(VolunteerRegistration registration, EventDtos.EventResponse event) {
            return new RegistrationResponse(registration.getId(), event,
                    UserDtos.UserResponse.from(registration.getVolunteer()), registration.getStatus(),
                    registration.getHoursLogged(), registration.getRegisteredAt());
        }
    }
}
