package org.volunteermatch.event;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.volunteermatch.user.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping("/events")
    List<EventDtos.EventResponse> list(@RequestParam(required = false) String q,
                                       @RequestParam(required = false) String city,
                                       @RequestParam(required = false) String category,
                                       @AuthenticationPrincipal UserPrincipal principal) {
        return service.list(q, city, category, principal == null ? null : principal.user());
    }

    @GetMapping("/events/{id}")
    EventDtos.EventResponse get(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return service.get(id, principal == null ? null : principal.user());
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventDtos.EventResponse create(@Valid @RequestBody EventDtos.EventRequest request,
                                   @AuthenticationPrincipal UserPrincipal principal) {
        return service.create(request, principal.user());
    }

    @PutMapping("/events/{id}")
    EventDtos.EventResponse update(@PathVariable Long id, @Valid @RequestBody EventDtos.EventRequest request,
                                   @AuthenticationPrincipal UserPrincipal principal) {
        return service.update(id, request, principal.user());
    }

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        service.delete(id, principal.user());
    }

    @GetMapping("/organizer/events")
    List<EventDtos.EventResponse> organizerEvents(@AuthenticationPrincipal UserPrincipal principal) {
        return service.organizerEvents(principal.user());
    }
}
