package sigebi.reportsandaudit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfExportStrategyTest {

    private PdfExportStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new PdfExportStrategy();
    }

    @Test
    void shouldExportPdf() {
        List<String> headers = List.of("ID", "Nombre", "Estado");
        List<List<String>> rows = List.of(
                List.of("1", "Equipo A", "Activo"),
                List.of("2", "Equipo B", "Inactivo")
        );

        byte[] result = strategy.export(headers, rows);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result.length > 100);
    }

    @Test
    void shouldExportPdfWithSingleRow() {
        List<String> headers = List.of("ID");
        List<List<String>> rows = List.of(List.of("1"));

        byte[] result = strategy.export(headers, rows);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldGetContentType() {
        assertEquals("application/pdf", strategy.getContentType());
    }

    @Test
    void shouldGetFileExtension() {
        assertEquals("pdf", strategy.getFileExtension());
    }
}
