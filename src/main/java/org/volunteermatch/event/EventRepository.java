package org.volunteermatch.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<VolunteerEvent, Long> {
    List<VolunteerEvent> findAllByOrderByStartsAtAsc();
    List<VolunteerEvent> findByOrganizerIdOrderByStartsAtAsc(Long organizerId);
}
