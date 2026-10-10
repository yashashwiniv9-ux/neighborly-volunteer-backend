package org.volunteermatch.registration;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.volunteermatch.event.EventDtos;
import org.volunteermatch.event.EventService;
import org.volunteermatch.user.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    private final RegistrationService registrations;
    private final EventService events;

    public RegistrationController(RegistrationService registrations, EventService events) {
        this.registrations = registrations;
        this.events = events;
    }

    @PostMapping("/events/{eventId}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    RegistrationDtos.RegistrationResponse register(@PathVariable Long eventId,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        return registrations.register(eventId, principal.user());
    }

    @DeleteMapping("/events/{eventId}/registrations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@PathVariable Long eventId, @AuthenticationPrincipal UserPrincipal principal) {
        registrations.cancel(eventId, principal.user());
    }

    @GetMapping("/me/registrations")
    List<RegistrationDtos.RegistrationResponse> mine(@AuthenticationPrincipal UserPrincipal principal) {
        return registrations.mine(principal.user());
    }

    @GetMapping("/events/{eventId}/participants")
    @PreAuthorize("hasRole('ORGANIZER')")
    List<RegistrationDtos.RegistrationResponse> participants(@PathVariable Long eventId,
                                                             @AuthenticationPrincipal UserPrincipal principal) {
        return events.participants(eventId, principal.user());
    }

    @PatchMapping("/events/{eventId}/registrations/{registrationId}/attendance")
    @PreAuthorize("hasRole('ORGANIZER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void markAttendance(@PathVariable Long eventId, @PathVariable Long registrationId,
                        @Valid @RequestBody EventDtos.AttendanceRequest request,
                        @AuthenticationPrincipal UserPrincipal principal) {
        registrations.markAttendance(eventId, registrationId, request.hours(), principal.user());
    }
}
