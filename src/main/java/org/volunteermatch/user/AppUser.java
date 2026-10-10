package org.volunteermatch.user;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(length = 120)
    private String city;

    @Column(length = 120)
    private String organizationName;

    @Column(length = 600)
    private String interests;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AppUser() {}

    public AppUser(String name, String email, String password, Role role, String city, String organizationName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.city = city;
        this.organizationName = organizationName;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getCity() { return city; }
    public String getOrganizationName() { return organizationName; }
    public String getInterests() { return interests; }
    public String getBio() { return bio; }
    public Instant getCreatedAt() { return createdAt; }
    public void updateProfile(String name, String city, String interests, String bio) {
        this.name = name;
        this.city = city;
        this.interests = interests;
        this.bio = bio;
    }
}
