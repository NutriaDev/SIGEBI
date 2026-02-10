package sigebi.equipment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sigebi.equipment.entities.EquipmentEntity;
import sigebi.equipment.service.EquipmentService;

import java.util.List;

@RestController
@RequestMapping("/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public List<EquipmentEntity> list() {
        return equipmentService.findAll();
    }
}