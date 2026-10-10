package org.volunteermatch.event;

import jakarta.persistence.*;
import org.volunteermatch.user.AppUser;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_events")
public class VolunteerEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false, length = 180)
    private String location;

    @Column(nullable = false, length = 120)
    private String city;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private int durationHours;

    @Column(nullable = false)
    private int capacity;

    @Column(length = 300)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private AppUser organizer;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected VolunteerEvent() {}

    public VolunteerEvent(String title, String description, String category, String location, String city,
                          LocalDateTime startsAt, int durationHours, int capacity, String imageUrl, AppUser organizer) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.city = city;
        this.startsAt = startsAt;
        this.durationHours = durationHours;
        this.capacity = capacity;
        this.imageUrl = imageUrl;
        this.organizer = organizer;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getCity() { return city; }
    public LocalDateTime getStartsAt() { return startsAt; }
    public int getDurationHours() { return durationHours; }
    public int getCapacity() { return capacity; }
    public String getImageUrl() { return imageUrl; }
    public AppUser getOrganizer() { return organizer; }
    public Instant getCreatedAt() { return createdAt; }
    public void update(String title, String description, String category, String location, String city,
                       LocalDateTime startsAt, int durationHours, int capacity, String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.city = city;
        this.startsAt = startsAt;
        this.durationHours = durationHours;
        this.capacity = capacity;
        this.imageUrl = imageUrl;
    }
}
