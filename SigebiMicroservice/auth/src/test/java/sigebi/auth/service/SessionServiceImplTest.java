package sigebi.auth.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.auth.entities.SessionEntity;
import sigebi.auth.exceptions.SessionNotActiveException;
import sigebi.auth.repository.SessionRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionInactiveTest {

    @Mock SessionRepository repository;
    @InjectMocks SessionServiceImpl service;

    @Test
    void validateActive_whenInactive_throws() {

        UUID id = UUID.randomUUID();

        SessionEntity session = SessionEntity.builder()
                .id(id)
                .active(false)
                .build();

        when(repository.findById(id))
                .thenReturn(Optional.of(session));

        assertThrows(SessionNotActiveException.class,
                () -> service.validateActive(id));
    }

    @Test
    void close_success_setsInactive() {

        UUID id = UUID.randomUUID();

        SessionEntity session = new SessionEntity();
        session.setActive(true);

        when(repository.findById(id))
                .thenReturn(Optional.of(session));

        service.close(id);

        verify(repository).save(session);
        assertFalse(session.getActive());
    }
}