package com.onenotebe.repository;

import com.onenotebe.model.TestNote;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the test entity to interact with JPA in tests.
 */
public interface TestNoteRepository extends JpaRepository<TestNote, Long> {
}