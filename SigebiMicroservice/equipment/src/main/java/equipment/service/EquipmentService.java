package equipment.service;

import equipment.dto_request.CreateEquipmentRequest;
import equipment.dto_request.UpdateEquipmentRequest;
import equipment.dto_response.EquipmentResponse;
import equipment.entities.AreaEntity;
import equipment.entities.ClassificationEntity;
import equipment.entities.EquipmentEntity;
import equipment.entities.LocationEntity;
import equipment.entities.ProviderEntity;
import equipment.entities.StateEntity;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.AreaRepository;
import equipment.repository.ClassificationRepository;
import equipment.repository.EquipmentRepository;
import equipment.repository.LocationRepository;
import equipment.repository.ProviderRepository;
import equipment.repository.StatesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final AreaRepository areaRepository;
    private final ClassificationRepository classificationRepository;
    private final ProviderRepository providerRepository;
    private final StatesRepository statesRepository;
    private final LocationRepository locationRepository;

    // Crear un nuevo equipo
    @Transactional
    public EquipmentResponse createEquipment(CreateEquipmentRequest request) {
        log.info("Creando nuevo equipo: {}", request.getSerie());

        // Validar serie duplicada
        if (equipmentRepository.existsBySerie(request.getSerie())) {
            log.warn("Intento de crear equipo con serie duplicada: {}", request.getSerie());
            throw new DuplicateResourceException("Ya existe un equipo con la serie: " + request.getSerie());
        }

        // Buscar área
        AreaEntity area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + request.getAreaId()));

        // Buscar clasificación
        ClassificationEntity classification = classificationRepository.findById(request.getClassificationId())
                .orElseThrow(() -> new ResourceNotFoundException("Clasificación no encontrada con ID: " + request.getClassificationId()));

        // Buscar proveedor (opcional)
        ProviderEntity provider = null;
        if (request.getProviderId() != null) {
            provider = providerRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + request.getProviderId()));
        }

        // Buscar estado
        StateEntity state = statesRepository.findById(request.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + request.getStateId()));

        // Buscar ubicación
        LocationEntity location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con ID: " + request.getLocationId()));

        EquipmentEntity equipment = EquipmentEntity.builder()
                .serie(request.getSerie())
                .name(request.getName())
                .brand(request.getBrand())
                .model(request.getModel())
                .invima(request.getInvima())
                .area(area)
                .classification(classification)
                .provider(provider)
                .state(state)
                .location(location)
                .riskLevel(request.getRiskLevel())
                .acquisitionDate(request.getAcquisitionDate())
                .usefulLife(request.getUsefulLife())
                .warrantyEnd(request.getWarrantyEnd())
                .maintenanceFrequency(request.getMaintenanceFrequency())
                .calibrationFrequency(request.getCalibrationFrequency())
                .active(true)
                .build();

        EquipmentEntity savedEquipment = equipmentRepository.save(equipment);
        log.info("Equipo creado exitosamente con ID: {}", savedEquipment.getEquipmentId());

        return mapToResponse(savedEquipment);
    }

    // Obtener todos los equipos
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAllEquipments() {
        log.info("Obteniendo todos los equipos");
        return equipmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener todos los equipos activos
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getActiveEquipments() {
        log.info("Obteniendo equipos activos");
        return equipmentRepository.findAllByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener un equipo por ID
    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentById(Long idEquipment) {
        log.info("Buscando equipo con ID: {}", idEquipment);
        EquipmentEntity equipment = equipmentRepository.findById(idEquipment)
                .orElseThrow(() -> {
                    log.error("Equipo no encontrado con ID: {}", idEquipment);
                    return new ResourceNotFoundException("Equipo no encontrado con ID: " + idEquipment);
                });
        return mapToResponse(equipment);
    }

    // Obtener un equipo por serie
    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentBySerie(String serie) {
        log.info("Buscando equipo con serie: {}", serie);
        EquipmentEntity equipment = equipmentRepository.findBySerieIgnoreCase(serie)
                .orElseThrow(() -> {
                    log.error("Equipo no encontrado con serie: {}", serie);
                    return new ResourceNotFoundException("Equipo no encontrado con serie: " + serie);
                });
        return mapToResponse(equipment);
    }

    // Obtener equipos por área
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getEquipmentsByArea(Long areaId) {
        log.info("Obteniendo equipos del área: {}", areaId);
        return equipmentRepository.findByAreaAreaId(areaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener equipos por clasificación
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getEquipmentsByClassification(Long classificationId) {
        log.info("Obteniendo equipos de la clasificación: {}", classificationId);
        return equipmentRepository.findByClassificationClassificationId(classificationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener equipos por proveedor
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getEquipmentsByProvider(Long providerId) {
        log.info("Obteniendo equipos del proveedor: {}", providerId);
        return equipmentRepository.findByProviderProviderId(providerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener equipos por estado
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getEquipmentsByState(Long stateId) {
        log.info("Obteniendo equipos con estado: {}", stateId);
        return equipmentRepository.findByStateStateId(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtener equipos por ubicación
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getEquipmentsByLocation(Long locationId) {
        log.info("Obteniendo equipos en ubicación: {}", locationId);
        return equipmentRepository.findByLocationLocationId(locationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Buscar equipos por nombre
    @Transactional(readOnly = true)
    public List<EquipmentResponse> searchEquipmentsByName(String name) {
        log.info("Buscando equipos con nombre: {}", name);
        return equipmentRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Actualizar un equipo
    @Transactional
    public EquipmentResponse updateEquipment(Long idEquipment, UpdateEquipmentRequest request) {
        log.info("Actualizando equipo con ID: {}", idEquipment);

        EquipmentEntity equipment = equipmentRepository.findById(idEquipment)
                .orElseThrow(() -> {
                    log.error("Equipo no encontrado con ID: {}", idEquipment);
                    return new ResourceNotFoundException("Equipo no encontrado con ID: " + idEquipment);
                });

        // Validar serie duplicada
        if (equipmentRepository.existsBySerieAndEquipmentIdNot(request.getSerie(), idEquipment)) {
            log.warn("Intento de actualizar equipo con serie duplicada: {}", request.getSerie());
            throw new DuplicateResourceException("Ya existe otro equipo con la serie: " + request.getSerie());
        }

        // Buscar área
        AreaEntity area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + request.getAreaId()));

        // Buscar clasificación
        ClassificationEntity classification = classificationRepository.findById(request.getClassificationId())
                .orElseThrow(() -> new ResourceNotFoundException("Clasificación no encontrada con ID: " + request.getClassificationId()));

        // Buscar proveedor (opcional)
        ProviderEntity provider = null;
        if (request.getProviderId() != null) {
            provider = providerRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + request.getProviderId()));
        }

        // Buscar estado
        StateEntity state = statesRepository.findById(request.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + request.getStateId()));

        // Buscar ubicación
        LocationEntity location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con ID: " + request.getLocationId()));

        equipment.setSerie(request.getSerie());
        equipment.setName(request.getName());
        equipment.setBrand(request.getBrand());
        equipment.setModel(request.getModel());
        equipment.setInvima(request.getInvima());
        equipment.setArea(area);
        equipment.setClassification(classification);
        equipment.setProvider(provider);
        equipment.setState(state);
        equipment.setLocation(location);
        equipment.setRiskLevel(request.getRiskLevel());
        equipment.setAcquisitionDate(request.getAcquisitionDate());
        equipment.setUsefulLife(request.getUsefulLife());
        equipment.setWarrantyEnd(request.getWarrantyEnd());
        equipment.setMaintenanceFrequency(request.getMaintenanceFrequency());
        equipment.setCalibrationFrequency(request.getCalibrationFrequency());

        if (request.getActive() != null) {
            equipment.setActive(request.getActive());
        }

        EquipmentEntity updatedEquipment = equipmentRepository.save(equipment);
        log.info("Equipo actualizado exitosamente: {}", updatedEquipment.getEquipmentId());

        return mapToResponse(updatedEquipment);
    }

    // Desactivar un equipo
    @Transactional
    public void deactivateEquipment(Long idEquipment) {
        log.info("Desactivando equipo con ID: {}", idEquipment);

        EquipmentEntity equipment = equipmentRepository.findById(idEquipment)
                .orElseThrow(() -> {
                    log.error("Equipo no encontrado con ID: {}", idEquipment);
                    return new ResourceNotFoundException("Equipo no encontrado con ID: " + idEquipment);
                });

        equipment.setActive(false);
        equipmentRepository.save(equipment);
        log.info("Equipo desactivado exitosamente: {}", idEquipment);
    }

    // Convertir entidad a DTO de respuesta
    private EquipmentResponse mapToResponse(EquipmentEntity equipment) {
        return EquipmentResponse.builder()
                .equipmentId(equipment.getEquipmentId())
                .serie(equipment.getSerie())
                .name(equipment.getName())
                .brand(equipment.getBrand())
                .model(equipment.getModel())
                .invima(equipment.getInvima())
                .areaId(equipment.getArea() != null ? equipment.getArea().getAreaId() : null)
                .areaName(equipment.getArea() != null ? equipment.getArea().getName() : null)
                .classificationId(equipment.getClassification() != null ? equipment.getClassification().getClassificationId() : null)
                .classificationName(equipment.getClassification() != null ? equipment.getClassification().getName() : null)
                .providerId(equipment.getProvider() != null ? equipment.getProvider().getProviderId() : null)
                .providerName(equipment.getProvider() != null ? equipment.getProvider().getName() : null)
                .stateId(equipment.getState() != null ? equipment.getState().getStateId() : null)
                .stateName(equipment.getState() != null ? equipment.getState().getName() : null)
                .locationId(equipment.getLocation() != null ? equipment.getLocation().getLocationId() : null)
                .locationName(equipment.getLocation() != null ? equipment.getLocation().getName() : null)
                .riskLevel(equipment.getRiskLevel())
                .acquisitionDate(equipment.getAcquisitionDate())
                .usefulLife(equipment.getUsefulLife())
                .warrantyEnd(equipment.getWarrantyEnd())
                .maintenanceFrequency(equipment.getMaintenanceFrequency())
                .calibrationFrequency(equipment.getCalibrationFrequency())
                .createdBy(equipment.getCreatedBy())
                .updatedBy(equipment.getUpdatedBy())
                .active(equipment.getActive())
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt())
                .build();
    }
}
