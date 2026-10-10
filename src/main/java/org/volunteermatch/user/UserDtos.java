package org.volunteermatch.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UserDtos {
    private UserDtos() {}

    public record RegisterRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Email @Size(max = 180) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            Role role,
            @Size(max = 120) String city,
            @Size(max = 120) String organizationName) {}

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record ProfileRequest(
            @NotBlank @Size(max = 120) String name,
            @Size(max = 120) String city,
            @Size(max = 600) String interests,
            @Size(max = 1000) String bio) {}
    public record UserResponse(Long id, String name, String email, Role role, String city,
                               String organizationName, String interests, String bio) {
        public static UserResponse from(AppUser user) {
            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(),
                    user.getCity(), user.getOrganizationName(), user.getInterests(), user.getBio());
        }
    }
}
