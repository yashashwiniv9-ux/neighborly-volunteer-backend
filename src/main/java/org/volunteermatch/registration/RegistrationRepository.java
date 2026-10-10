package org.volunteermatch.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<VolunteerRegistration, Long> {
    Optional<VolunteerRegistration> findByEventIdAndVolunteerId(Long eventId, Long volunteerId);
    List<VolunteerRegistration> findByVolunteerIdOrderByRegisteredAtDesc(Long volunteerId);
    List<VolunteerRegistration> findByEventIdOrderByRegisteredAtAsc(Long eventId);
    void deleteByEventId(Long eventId);
    long countByEventIdAndStatusIn(Long eventId, List<RegistrationStatus> statuses);
    long countByVolunteerIdAndStatus(Long volunteerId, RegistrationStatus status);
    @Query("select coalesce(sum(r.hoursLogged), 0) from VolunteerRegistration r where r.volunteer.id = :volunteerId")
    long sumHoursLoggedByVolunteerId(@Param("volunteerId") Long volunteerId);
}
