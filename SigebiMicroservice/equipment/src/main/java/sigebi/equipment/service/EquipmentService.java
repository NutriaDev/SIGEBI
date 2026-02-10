package sigebi.equipment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.equipment.entities.EquipmentEntity;
import sigebi.equipment.repository.EquipmentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public List<EquipmentEntity> findAll() {
        return equipmentRepository.findAll();
    }
}