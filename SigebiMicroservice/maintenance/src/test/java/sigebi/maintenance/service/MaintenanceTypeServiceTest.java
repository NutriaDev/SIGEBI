package sigebi.maintenance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sigebi.maintenance.entities.MaintenanceTypeEntity;
import sigebi.maintenance.repository.MaintenanceTypeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceTypeServiceTest {

    @Mock
    private MaintenanceTypeRepository repository;

    @InjectMocks
    private MaintenanceTypeService service;

    @Test
    void shouldGetAllTypes() {
        MaintenanceTypeEntity type1 = MaintenanceTypeEntity.builder()
                .idType(1L).name("Preventivo").build();
        MaintenanceTypeEntity type2 = MaintenanceTypeEntity.builder()
                .idType(2L).name("Correctivo").build();
        when(repository.findAll()).thenReturn(List.of(type1, type2));

        List<MaintenanceTypeEntity> result = service.getAllTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Preventivo", result.get(0).getName());
        assertEquals("Correctivo", result.get(1).getName());
        verify(repository).findAll();
    }

    @Test
    void shouldGetEmptyListWhenNoTypes() {
        when(repository.findAll()).thenReturn(List.of());

        List<MaintenanceTypeEntity> result = service.getAllTypes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void shouldSaveType() {
        MaintenanceTypeEntity type = MaintenanceTypeEntity.builder()
                .name("Nuevo Tipo").build();
        MaintenanceTypeEntity saved = MaintenanceTypeEntity.builder()
                .idType(1L).name("Nuevo Tipo").build();
        when(repository.save(type)).thenReturn(saved);

        MaintenanceTypeEntity result = service.saveType(type);

        assertNotNull(result);
        assertEquals(1L, result.getIdType());
        assertEquals("Nuevo Tipo", result.getName());
        verify(repository).save(type);
    }
}
