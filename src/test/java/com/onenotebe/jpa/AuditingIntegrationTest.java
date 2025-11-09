package com.onenotebe.jpa;

import com.onenotebe.config.JpaConfig;
import com.onenotebe.model.TestNote;
import com.onenotebe.repository.TestNoteRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JPA auditing using the BaseEntity superclass.
 */
@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(properties = {
        // Use H2 for tests and ensure schema is managed for each test run
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver"
})
class AuditingIntegrationTest {

    private final TestNoteRepository repository;

    AuditingIntegrationTest(TestNoteRepository repository) {
        this.repository = repository;
    }

    @Test
    @DisplayName("Saving entity generates ID and populates timestamps")
    void save_shouldGenerateIdAndPopulateTimestamps() {
        var note = TestNote.builder().title("Hello") .build();

        var saved = repository.save(note);

        assertNotNull(saved.getId(), "ID should be generated");
        assertNotNull(saved.getCreatedAt(), "createdAt should be populated");
        assertNotNull(saved.getUpdatedAt(), "updatedAt should be populated");
    }

    @Test
    @DisplayName("Updating entity refreshes updatedAt and preserves createdAt (immutable)")
    void update_shouldRefreshUpdatedAtAndKeepCreatedAtUnchanged() throws InterruptedException {
        var note = repository.save(TestNote.builder().title("First") .build());

        var initialCreated = note.getCreatedAt();
        var initialUpdated = note.getUpdatedAt();

        assertNotNull(initialCreated);
        assertNotNull(initialUpdated);

        // Ensure time progresses so updatedAt changes visibly
        Thread.sleep(20L);

        note.setTitle("Updated title");
        // Attempt to modify createdAt; should be ignored due to updatable=false
        note.setCreatedAt(Optional.ofNullable(initialCreated).map(ts -> ts.plusSeconds(3600)).orElse(Instant.now()));

        var saved = repository.save(note);
        var reloaded = repository.findById(saved.getId()).orElseThrow(() -> new IllegalStateException("Saved entity not found"));

        assertEquals(initialCreated, reloaded.getCreatedAt(), "createdAt must remain unchanged after update");
        assertTrue(reloaded.getUpdatedAt().isAfter(initialUpdated), "updatedAt must be refreshed on update");
    }
}