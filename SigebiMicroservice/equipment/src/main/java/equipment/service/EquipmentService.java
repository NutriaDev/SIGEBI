package equipment.service;

import equipment.dto_request.CreateEquipmentRequest;
import equipment.dto_request.UpdateEquipmentRequest;
import equipment.dto_response.EquipmentResponse;
import equipment.entities.*;
import equipment.exception.DuplicateResourceException;
import equipment.exception.ResourceNotFoundException;
import equipment.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // ================= CREATE =================

    @Transactional
    public EquipmentResponse createEquipment(CreateEquipmentRequest request) {

        // Validar serie duplicada
        if (equipmentRepository.existsBySerie(request.getSerie())) {
            throw new DuplicateResourceException("Ya existe un equipo con la serie: " + request.getSerie());
        }

        // Buscar área
        AreaEntity area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Área no encontrada con ID: " + request.getAreaId()));

        // Buscar clasificación
        ClassificationEntity classification = classificationRepository.findById(request.getClassificationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Clasificación no encontrada con ID: " + request.getClassificationId()));

        // Buscar proveedor (opcional)
        ProviderEntity provider = null;
        if (request.getProviderId() != null) {
            provider = providerRepository.findById(request.getProviderId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Proveedor no encontrado con ID: " + request.getProviderId()));
        }

        // Buscar estado
        StateEntity state = statesRepository.findById(request.getStateId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Estado no encontrado con ID: " + request.getStateId()));

        // Buscar ubicación
        LocationEntity location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ubicación no encontrada con ID: " + request.getLocationId()));

        // Crear equipo
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
                .createdBy(request.getResponsibleUserId())   // 👈 FALTA ESTO
                .active(true)
                .build();

        EquipmentEntity savedEquipment = equipmentRepository.save(equipment);

        return mapToResponse(savedEquipment);
    }

    // ================= GET ALL =================

    @Transactional(readOnly = true)
    public Page<EquipmentResponse> getAllEquipments(Pageable pageable) {

        return equipmentRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    // ================= GET ACTIVE =================

    @Transactional(readOnly = true)
    public Page<EquipmentResponse> getActiveEquipments(Pageable pageable) {

        return equipmentRepository
                .findAllByActive(true, pageable)
                .map(this::mapToResponse);
    }

    // ================= GET BY ID =================

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentById(Long idEquipment) {

        return mapToResponse(findEquipmentOrThrow(idEquipment));
    }

    // ================= GET BY SERIE =================

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentBySerie(String serie) {

        EquipmentEntity equipment = equipmentRepository
                .findBySerieIgnoreCase(serie)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Equipo no encontrado con serie: " + serie));

        return mapToResponse(equipment);
    }

    // ================= FILTERS =================

    // ================= FILTERS BY NAME =================

    @Transactional(readOnly = true)
    public Page<EquipmentResponse> getEquipmentsByAreaName(String areaName, Pageable pageable) {
        return equipmentRepository
                .findByAreaNameContainingIgnoreCase(areaName, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentResponse> getEquipmentsByClassificationName(String classificationName, Pageable pageable) {
        return equipmentRepository
                .findByClassificationNameContainingIgnoreCase(classificationName, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentResponse> getEquipmentsByProviderName(String providerName, Pageable pageable) {
        return equipmentRepository
                .findByProviderNameContainingIgnoreCase(providerName, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentResponse> getEquipmentsByStateName(String stateName, Pageable pageable) {
        return equipmentRepository
                .findByStateNameContainingIgnoreCase(stateName, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentResponse> getEquipmentsByLocationName(String locationName, Pageable pageable) {
        return equipmentRepository
                .findByLocationNameContainingIgnoreCase(locationName, pageable)
                .map(this::mapToResponse);
    }
    @Transactional(readOnly = true)
    public Page<EquipmentResponse> searchEquipmentsByName(String name, Pageable pageable) {

        return equipmentRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(this::mapToResponse);
    }

    // ================= UPDATE =================

    @Transactional
    public EquipmentResponse updateEquipment(Long idEquipment, UpdateEquipmentRequest request) {

        log.info("Actualizando equipo con ID: {}", idEquipment);

        EquipmentEntity equipment = findEquipmentOrThrow(idEquipment);

        // Validar serie duplicada
        validateDuplicateSerie(request.getSerie(), idEquipment);

        equipment.setSerie(request.getSerie());
        equipment.setName(request.getName());
        equipment.setBrand(request.getBrand());
        equipment.setModel(request.getModel());
        equipment.setInvima(request.getInvima());

        // Relaciones
        equipment.setArea(findArea(request.getAreaId()));
        equipment.setClassification(findClassification(request.getClassificationId()));
        equipment.setProvider(findProvider(request.getProviderId()));
        equipment.setState(findState(request.getStateId()));
        equipment.setLocation(findLocation(request.getLocationId()));

        // Datos del equipo
        equipment.setRiskLevel(request.getRiskLevel());
        equipment.setAcquisitionDate(request.getAcquisitionDate());
        equipment.setUsefulLife(request.getUsefulLife());
        equipment.setWarrantyEnd(request.getWarrantyEnd());
        equipment.setMaintenanceFrequency(request.getMaintenanceFrequency());
        equipment.setCalibrationFrequency(request.getCalibrationFrequency());

        // Auditoría
        equipment.setUpdatedBy(request.getUpdatedBy());

        if (request.getActive() != null) {
            equipment.setActive(request.getActive());
        }

        EquipmentEntity updatedEquipment = equipmentRepository.save(equipment);

        log.info("Equipo actualizado exitosamente con ID: {}", updatedEquipment.getEquipmentId());

        return mapToResponse(updatedEquipment);
    }

    // ================= DEACTIVATE =================

    @Transactional
    public void deactivateEquipment(Long idEquipment) {

        EquipmentEntity equipment = findEquipmentOrThrow(idEquipment);

        if (!equipment.getActive()) {
            throw new IllegalStateException("El equipo ya está desactivado");
        }

        equipment.setActive(false);

        equipmentRepository.save(equipment);
    }

    // ================= VALIDATIONS =================

    private void validateDuplicateSerie(String serie, Long idToExclude) {

        boolean exists;

        if (idToExclude == null) {
            exists = equipmentRepository.existsBySerie(serie);
        } else {
            exists = equipmentRepository.existsBySerieAndEquipmentIdNot(serie, idToExclude);
        }

        if (exists) {
            throw new DuplicateResourceException("Ya existe un equipo con la serie: " + serie);
        }
    }

    private EquipmentEntity findEquipmentOrThrow(Long idEquipment) {

        return equipmentRepository.findById(idEquipment)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Equipo no encontrado con ID: " + idEquipment));
    }

    private AreaEntity findArea(Long id) {

        return areaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Área no encontrada con ID: " + id));
    }

    private ClassificationEntity findClassification(Long id) {

        return classificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Clasificación no encontrada con ID: " + id));
    }

    private ProviderEntity findProvider(Long id) {

        if (id == null) return null;

        return providerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
    }

    private StateEntity findState(Long id) {

        return statesRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Estado no encontrado con ID: " + id));
    }

    private LocationEntity findLocation(Long id) {

        return locationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ubicación no encontrada con ID: " + id));
    }

    // ================= MAPPER =================

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