package sigebi.reportsandaudit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sigebi.reportsandaudit.dto_request.SparePartItem;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceReportPdfGeneratorTest {

    private ServiceReportPdfGenerator generator;

    @BeforeEach
    void setup() {
        generator = new ServiceReportPdfGenerator();
    }

    @Test
    void shouldGeneratePdfWithAllFields() {
        List<SparePartItem> spareParts = List.of(
                SparePartItem.builder().quantity(2).reference("REF-001").description("Resistor").build(),
                SparePartItem.builder().quantity(1).reference("REF-002").description("Capacitor").build()
        );

        byte[] result = generator.generate(
                1L,
                "Diagnostico de prueba",
                "Actividades realizadas",
                "Observaciones del reporte",
                spareParts,
                100L,
                200L,
                "SN-001",
                LocalDateTime.of(2025, 1, 15, 10, 30),
                "Juan Perez",
                "PREVENTIVO",
                "UCI Adultos"
        );

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result.length > 1000);
    }

    @Test
    void shouldGeneratePdfWithMinimalFields() {
        byte[] result = generator.generate(
                null, null, null, null, null,
                null, null, null, null, null, null, null
        );

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldGeneratePdfWithEmptySpareParts() {
        byte[] result = generator.generate(
                1L, "Diagnostico", "Actividades", "Observaciones",
                List.of(),
                100L, 200L, "SN-001",
                LocalDateTime.now(), "Juan Perez", "CORRECTIVO", "Lab A"
        );

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldGeneratePdfWithSingleSparePart() {
        List<SparePartItem> spareParts = List.of(
                SparePartItem.builder().quantity(1).reference("REF-001").description("Parte unica").build()
        );

        byte[] result = generator.generate(
                1L, "Diagnostico", "Actividades", "Observaciones",
                spareParts,
                100L, 200L, "SN-001",
                LocalDateTime.now(), "Juan Perez", "CALIBRACION", "Quirófano"
        );

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
