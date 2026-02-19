package equipment.service;

import equipment.dto_request.CreateEquipmentRequest;
import equipment.dto_request.UpdateEquipmentRequest;
import equipment.dto_response.EquipmentResponse;
import jakarta.validation.Valid;

import java.util.List;

public class EquipmentService {
    public List<EquipmentResponse> getAllEquipment() {
        return List.of();
    }
    public Object getEquipmentById(Long id) {
        return List.of();
    }
    public Object updateEquipment(Long id, @Valid UpdateEquipmentRequest request) {
        return List.of();
    }
    public Object createEquipment(@Valid CreateEquipmentRequest request) {
        return null;
    }
    public Object toggleEquipmentStatus(Long id, boolean active) {
        return List.of();
    }
    public List<Object> inactiveEquipment(Long id) {
        return List.of();
    }
}
