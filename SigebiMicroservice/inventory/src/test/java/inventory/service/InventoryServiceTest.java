package inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.client.EquipmentClient;
import inventory.dto_request.InventoryDetailRequest;
import inventory.dto_request.InventoryRequest;
import inventory.dto_response.ApiResponse;
import inventory.dto_response.EquipmentResponse;
import inventory.exception.BusinessException;
import inventory.exception.EquipmentNotFoundException;
import inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ObjectMapper objectMapper;

    @InjectMocks
    private InventoryService service;

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
    }

    @Test
    void shouldCreateInventorySuccessfully() {
        InventoryRequest req = new InventoryRequest(
                1L,
                "Lab",
                LocalDate.now(),
                "Obs",
                "1",
                "ADMIN",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        when(equipmentClient.getEquipmentById(1L)).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipment);

        String result = service.createPhysicalInventory(req);

        assertEquals("Inventario realizado correctamente", result);
        verify(inventoryRepository).save(any());
    }

    // 🔴 RAMA NUEVA: date == null → debe usar LocalDate.now() internamente
    @Test
    void shouldCreateInventoryWithNullDate() {
        InventoryRequest req = new InventoryRequest(
                1L, "Lab", null, "Obs", "1", "ADMIN",
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
                1L, "Lab", null, "Obs", "1", "ADMIN", List.of()
        );

        assertThrows(BusinessException.class,
                () -> service.createPhysicalInventory(req));
    }

    @Test
    void shouldThrowWhenEquipmentClientThrowsException() {
        InventoryRequest req = new InventoryRequest(
                1L, "Lab", null, "Obs", "1", "ADMIN",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        when(equipmentClient.getEquipmentById(1L))
                .thenThrow(new RuntimeException());

        assertThrows(EquipmentNotFoundException.class,
                () -> service.createPhysicalInventory(req));
    }

    // 🔴 RAMA NUEVA: response es null → EquipmentNotFoundException
    @Test
    void shouldThrowWhenResponseIsNull() {
        InventoryRequest req = new InventoryRequest(
                1L, "Lab", null, "Obs", "1", "ADMIN",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        when(equipmentClient.getEquipmentById(1L)).thenReturn(null);

        assertThrows(EquipmentNotFoundException.class,
                () -> service.createPhysicalInventory(req));
    }

    // 🔴 RAMA NUEVA: response.getBody() es null → EquipmentNotFoundException
    @Test
    void shouldThrowWhenResponseBodyIsNull() {
        InventoryRequest req = new InventoryRequest(
                1L, "Lab", null, "Obs", "1", "ADMIN",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        ApiResponse emptyResponse = new ApiResponse();
        emptyResponse.setBody(null);

        when(equipmentClient.getEquipmentById(1L)).thenReturn(emptyResponse);

        assertThrows(EquipmentNotFoundException.class,
                () -> service.createPhysicalInventory(req));
    }

    // 🔴 RAMA NUEVA: equipo con locationId distinto al inventario (extraInPhysical no vacío)
    @Test
    void shouldCreateInventoryWithEquipmentInDifferentLocation() {
        InventoryRequest req = new InventoryRequest(
                1L, "Lab", LocalDate.now(), "Obs", "1", "ADMIN",
                List.of(new InventoryDetailRequest(1L, "OK", "obs"))
        );

        EquipmentResponse equipmentOtherLocation = new EquipmentResponse();
        equipmentOtherLocation.setEquipmentId(1L);
        equipmentOtherLocation.setLocationId(99L); // locationId diferente al del inventario

        when(equipmentClient.getEquipmentById(1L)).thenReturn(apiResponse);
        when(objectMapper.convertValue(any(), eq(EquipmentResponse.class)))
                .thenReturn(equipmentOtherLocation);

        String result = service.createPhysicalInventory(req);

        assertEquals("Inventario realizado correctamente", result);
    }
}