package inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.client.EquipmentClient;
import inventory.client.LocationClient;
import inventory.client.UserClient;
import inventory.dto_request.InventoryDetailRequest;
import inventory.dto_request.InventoryRequest;
import inventory.dto_response.ApiResponse;
import inventory.dto_response.EquipmentResponse;
import inventory.dto_response.LocationResponse;
import inventory.dto_response.UserResponse;
import inventory.exception.BusinessException;
import inventory.exception.EquipmentNotFoundException;
import inventory.kafka.InventoryEventProducer;
import inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private EquipmentClient equipmentClient;

    @Mock
    private UserClient userClient;

    @Mock
    private LocationClient locationClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InventoryEventProducer inventoryEventProducer;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InventoryService service;

    private ApiResponse apiResponse;
    private ApiResponse locationApiResponse;
    private EquipmentResponse equipment;

    @BeforeEach
    void setup() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(1L);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);

        equipment = new EquipmentResponse();
        equipment.setEquipmentId(1L);
        equipment.setLocationId(1L);

        apiResponse = new ApiResponse();
        apiResponse.setBody(Map.of(
                "equipmentId", 1,
                "locationId", 1
        ));

        UserResponse userResp = new UserResponse();
        userResp.setName("Test User");

        locationApiResponse = new ApiResponse();
        locationApiResponse.setBody(Map.of("name", "Lab"));

        when(userClient.getUserById(1L)).thenReturn(userResp);
        when(locationClient.getLocationById(1L)).thenReturn(locationApiResponse);
        when(objectMapper.convertValue(any(), eq(LocationResponse.class)))
                .thenReturn(createLocationResponse());
    }

    private LocationResponse createLocationResponse() {
        var l = new LocationResponse();
        l.setName("Lab");
        return l;
    }

    @Test
    void shouldCreateInventorySuccessfully() {
        InventoryRequest req = new InventoryRequest(
                1L,
                LocalDate.now(),
                "Obs",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        when(equipmentClient.getEquipmentById(1L)).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipment);

        String result = service.createPhysicalInventory(req);

        assertEquals("Inventario realizado correctamente", result);
        verify(inventoryRepository).save(any());
    }

    @Test
    void shouldCreateInventoryWithNullDate() {
        InventoryRequest req = new InventoryRequest(
                1L, null, "Obs",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        when(equipmentClient.getEquipmentById(1L)).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipment);

        String result = service.createPhysicalInventory(req);

        assertEquals("Inventario realizado correctamente", result);
    }

    @Test
    void shouldThrowWhenNoDetails() {
        InventoryRequest req = new InventoryRequest(
                1L, null, "Obs", List.of()
        );

        assertThrows(BusinessException.class,
                () -> service.createPhysicalInventory(req));
    }

    @Test
    void shouldThrowWhenEquipmentClientThrowsException() {
        InventoryRequest req = new InventoryRequest(
                1L, null, "Obs",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        when(equipmentClient.getEquipmentById(1L))
                .thenThrow(new RuntimeException());

        assertThrows(EquipmentNotFoundException.class,
                () -> service.createPhysicalInventory(req));
    }

    @Test
    void shouldThrowWhenResponseIsNull() {
        InventoryRequest req = new InventoryRequest(
                1L, null, "Obs",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        when(equipmentClient.getEquipmentById(1L)).thenReturn(null);

        assertThrows(EquipmentNotFoundException.class,
                () -> service.createPhysicalInventory(req));
    }

    @Test
    void shouldThrowWhenResponseBodyIsNull() {
        InventoryRequest req = new InventoryRequest(
                1L, null, "Obs",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        ApiResponse emptyResponse = new ApiResponse();
        emptyResponse.setBody(null);

        when(equipmentClient.getEquipmentById(1L)).thenReturn(emptyResponse);

        assertThrows(EquipmentNotFoundException.class,
                () -> service.createPhysicalInventory(req));
    }

    @Test
    void shouldCreateInventoryWithEquipmentInDifferentLocation() {
        InventoryRequest req = new InventoryRequest(
                1L, LocalDate.now(), "Obs",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        EquipmentResponse equipmentOtherLocation = new EquipmentResponse();
        equipmentOtherLocation.setEquipmentId(1L);
        equipmentOtherLocation.setLocationId(99L);

        when(equipmentClient.getEquipmentById(1L)).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipmentOtherLocation);

        String result = service.createPhysicalInventory(req);

        assertEquals("Inventario realizado correctamente", result);
    }
}