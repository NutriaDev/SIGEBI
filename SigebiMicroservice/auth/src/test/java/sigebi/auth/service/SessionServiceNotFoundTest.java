package sigebi.auth.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.auth.exceptions.SessionNotFoundException;
import sigebi.auth.repository.SessionRepository;


import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceNotFoundTest {

    @Mock
    SessionRepository repository;

    @InjectMocks
    SessionServiceImpl service;

    @Test
    void validateActive_whenSessionNotFound_throws() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class,
                () -> service.validateActive(id));
    }
}