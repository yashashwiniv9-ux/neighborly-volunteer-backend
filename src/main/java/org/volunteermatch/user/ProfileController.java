package org.volunteermatch.user;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.volunteermatch.registration.RegistrationRepository;
import org.volunteermatch.registration.RegistrationStatus;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProfileController {
    private final UserRepository users;
    private final RegistrationRepository registrations;

    public ProfileController(UserRepository users, RegistrationRepository registrations) {
        this.users = users;
        this.registrations = registrations;
    }

    @GetMapping("/me")
    UserDtos.UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return UserDtos.UserResponse.from(principal.user());
    }

    @PutMapping("/me")
    UserDtos.UserResponse update(@AuthenticationPrincipal UserPrincipal principal,
                                 @Valid @RequestBody UserDtos.ProfileRequest request) {
        AppUser user = users.findById(principal.user().getId()).orElseThrow();
        user.updateProfile(request.name().trim(), clean(request.city()), clean(request.interests()), clean(request.bio()));
        return UserDtos.UserResponse.from(users.save(user));
    }

    @GetMapping("/me/stats")
    @PreAuthorize("hasRole('VOLUNTEER')")
    Map<String, Long> stats(@AuthenticationPrincipal UserPrincipal principal) {
        Long id = principal.user().getId();
        return Map.of(
                "hours", registrations.sumHoursLoggedByVolunteerId(id),
                "upcoming", registrations.countByVolunteerIdAndStatus(id, RegistrationStatus.REGISTERED),
                "completed", registrations.countByVolunteerIdAndStatus(id, RegistrationStatus.ATTENDED));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
