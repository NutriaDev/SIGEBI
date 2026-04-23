package inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.client.EquipmentClient;
import inventory.dto_request.MovementRequest;
import inventory.dto_response.ApiResponse;
import inventory.dto_response.EquipmentResponse;
import inventory.exception.BusinessException;
import inventory.exception.EquipmentNotFoundException;
import inventory.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private EquipmentClient equipmentClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MovementService service;

    private ApiResponse apiResponse;
    private EquipmentResponse equipment;

    @BeforeEach
    void setup() {
        equipment = new EquipmentResponse();
        equipment.setEquipmentId(1L);
        equipment.setLocationId(1L);

        apiResponse = new ApiResponse();
        apiResponse.setBody(Map.of(
                "equipmentId", 1,
                "locationId", 1
        ));

        var auth = new UsernamePasswordAuthenticationToken(99L, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldRegisterMovementSuccessfully() {
        MovementRequest req = new MovementRequest("EQ-123", 1L, 2L, "TRANSFER");

        when(equipmentClient.findBySerial("EQ-123")).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipment);

        service.registerMovement(req);

        verify(movementRepository).save(any());
        verify(equipmentClient).updateLocation(eq(1L), any());
    }

    @Test
    void shouldThrowWhenLocationMismatch() {
        equipment.setLocationId(99L);
        MovementRequest req = new MovementRequest("EQ-123", 1L, 2L, "TRANSFER");

        when(equipmentClient.findBySerial("EQ-123")).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipment);

        assertThrows(BusinessException.class,
                () -> service.registerMovement(req));
    }

    @Test
    void shouldThrowWhenEquipmentClientThrowsException() {
        MovementRequest req = new MovementRequest("EQ-123", 1L, 2L, "TRANSFER");

        when(equipmentClient.findBySerial("EQ-123"))
                .thenThrow(new RuntimeException());

        assertThrows(EquipmentNotFoundException.class,
                () -> service.registerMovement(req));
    }

    // 🔴 RAMA NUEVA: response es null → EquipmentNotFoundException
    @Test
    void shouldThrowWhenResponseIsNull() {
        MovementRequest req = new MovementRequest("EQ-123", 1L, 2L, "TRANSFER");

        when(equipmentClient.findBySerial("EQ-123")).thenReturn(null);

        assertThrows(EquipmentNotFoundException.class,
                () -> service.registerMovement(req));
    }

    // 🔴 RAMA NUEVA: response.getBody() es null → EquipmentNotFoundException
    @Test
    void shouldThrowWhenResponseBodyIsNull() {
        MovementRequest req = new MovementRequest("EQ-123", 1L, 2L, "TRANSFER");

        ApiResponse emptyResponse = new ApiResponse();
        emptyResponse.setBody(null);
        when(equipmentClient.findBySerial("EQ-123")).thenReturn(emptyResponse);

        assertThrows(EquipmentNotFoundException.class,
                () -> service.registerMovement(req));
    }

    // 🔴 RAMA NUEVA: updateLocation lanza excepción → BusinessException
    @Test
    void shouldThrowWhenUpdateLocationFails() {
        MovementRequest req = new MovementRequest("EQ-123", 1L, 2L, "TRANSFER");

        when(equipmentClient.findBySerial("EQ-123")).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipment);
        doThrow(new RuntimeException("timeout"))
                .when(equipmentClient).updateLocation(eq(1L), any());

        assertThrows(BusinessException.class,
                () -> service.registerMovement(req));
    }

    // 🔴 RAMA NUEVA: findEquipmentBySerial (método público no testeado)
    @Test
    void shouldFindEquipmentBySerialSuccessfully() {
        when(equipmentClient.findBySerial("EQ-123")).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipment);

        EquipmentResponse result = service.findEquipmentBySerial("EQ-123");

        assertNotNull(result);
        assertEquals(1L, result.getEquipmentId());
    }

    // 🔴 RAMA NUEVA: findEquipmentBySerial cuando no encuentra el equipo
    @Test
    void shouldThrowWhenFindEquipmentBySerialFails() {
        when(equipmentClient.findBySerial("EQ-999"))
                .thenThrow(new RuntimeException());

        assertThrows(EquipmentNotFoundException.class,
                () -> service.findEquipmentBySerial("EQ-999"));
    }
}