package org.volunteermatch.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.volunteermatch.event.EventRepository;
import org.volunteermatch.event.VolunteerEvent;
import org.volunteermatch.user.AppUser;
import org.volunteermatch.user.Role;
import org.volunteermatch.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class SampleDataInitializer {
    @Bean
    @ConditionalOnProperty(name = "app.sample-data.enabled", havingValue = "true")
    CommandLineRunner sampleData(UserRepository users, EventRepository events, PasswordEncoder encoder) {
        return args -> {
            if (events.count() > 0) return;
            AppUser organizer = users.findByEmailIgnoreCase("hello@neighborhood.org")
                    .orElseGet(() -> users.save(new AppUser("Jordan Lee", "hello@neighborhood.org",
                            encoder.encode("volunteer123"), Role.ORGANIZER, "Portland", "Neighborhood Collective")));
            LocalDateTime now = LocalDateTime.now();
            events.saveAll(List.of(
                    new VolunteerEvent("Restore the river trail", "Help our crew clear winter debris, plant native flowers, and make the riverside trail welcoming for everyone. All tools and a morning snack are provided.", "Environment", "Eastbank Esplanade", "Portland", now.plusDays(2).withHour(9).withMinute(0), 3, 18, null, organizer),
                    new VolunteerEvent("Pack meals for neighbors", "Join a friendly assembly line preparing fresh, nourishing meal kits for local families. No experience needed — just bring your energy and we'll show you the rest.", "Food access", "Community Food Hub", "Portland", now.plusDays(4).withHour(10).withMinute(30), 2, 24, null, organizer),
                    new VolunteerEvent("Read together: after-school club", "Spend an afternoon helping elementary students discover the joy of reading. Read aloud, help with homework, or simply be an encouraging presence.", "Education", "Cedar Grove Library", "Portland", now.plusDays(6).withHour(15).withMinute(0), 2, 8, null, organizer),
                    new VolunteerEvent("A brighter block, together", "Give a neighborhood park a little love. We'll paint benches, tidy the garden beds, and share lunch with neighbors when we're done.", "Community", "Laurelhurst Park", "Portland", now.plusDays(8).withHour(9).withMinute(30), 4, 30, null, organizer),
                    new VolunteerEvent("Community pantry restock", "Sort generous donations, restock pantry shelves, and help visitors find what they need in a welcoming, judgment-free space.", "Food access", "Rose City Community Pantry", "Portland", now.plusDays(10).withHour(11).withMinute(0), 3, 12, null, organizer)
            ));
        };
    }
}
