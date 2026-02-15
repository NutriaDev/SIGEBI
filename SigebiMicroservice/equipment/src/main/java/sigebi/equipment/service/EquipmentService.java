package service;

import dto_request.CreateEquipmentRequest;
import dto_request.UpdateEquipmentRequest;
import dto_response.EquipmentResponse;
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
    public List<Object> deleteEquipment(Long id) {
        return List.of();
    }
}
