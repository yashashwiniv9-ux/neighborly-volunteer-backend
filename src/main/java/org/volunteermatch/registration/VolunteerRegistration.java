package org.volunteermatch.registration;

import jakarta.persistence.*;
import org.volunteermatch.event.VolunteerEvent;
import org.volunteermatch.user.AppUser;

import java.time.Instant;

@Entity
@Table(name = "registrations", uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "volunteer_id"}))
public class VolunteerRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private VolunteerEvent event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private AppUser volunteer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    @Column(nullable = false)
    private int hoursLogged;

    @Column(nullable = false, updatable = false)
    private Instant registeredAt = Instant.now();

    protected VolunteerRegistration() {}

    public VolunteerRegistration(VolunteerEvent event, AppUser volunteer) {
        this.event = event;
        this.volunteer = volunteer;
    }

    public Long getId() { return id; }
    public VolunteerEvent getEvent() { return event; }
    public AppUser getVolunteer() { return volunteer; }
    public RegistrationStatus getStatus() { return status; }
    public int getHoursLogged() { return hoursLogged; }
    public Instant getRegisteredAt() { return registeredAt; }
    public void cancel() { this.status = RegistrationStatus.CANCELLED; }
    public void markAttended(int hoursLogged) {
        this.status = RegistrationStatus.ATTENDED;
        this.hoursLogged = hoursLogged;
    }
    public void reRegister() { this.status = RegistrationStatus.REGISTERED; }
}
