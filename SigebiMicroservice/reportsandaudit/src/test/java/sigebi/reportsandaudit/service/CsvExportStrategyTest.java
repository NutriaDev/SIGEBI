package sigebi.reportsandaudit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExportStrategyTest {

    private CsvExportStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new CsvExportStrategy();
    }

    @Test
    void shouldExportCsv() {
        List<String> headers = List.of("ID", "Nombre", "Estado");
        List<List<String>> rows = List.of(
                List.of("1", "Equipo A", "Activo"),
                List.of("2", "Equipo B", "Inactivo")
        );

        byte[] result = strategy.export(headers, rows);

        assertNotNull(result);
        assertTrue(result.length > 0);
        String csv = new String(result);
        assertTrue(csv.startsWith("ID,Nombre,Estado\n"));
        assertTrue(csv.contains("1,Equipo A,Activo\n"));
        assertTrue(csv.contains("2,Equipo B,Inactivo\n"));
    }

    @Test
    void shouldReturnEmptyBytesWhenRowsEmpty() {
        List<String> headers = List.of("ID", "Nombre");
        List<List<String>> rows = List.of();

        byte[] result = strategy.export(headers, rows);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void shouldExportSingleRow() {
        List<String> headers = List.of("ID");
        List<List<String>> rows = List.of(List.of("1"));

        byte[] result = strategy.export(headers, rows);

        assertNotNull(result);
        String csv = new String(result);
        assertEquals("ID\n1\n", csv);
    }

    @Test
    void shouldGetContentType() {
        assertEquals("text/csv", strategy.getContentType());
    }

    @Test
    void shouldGetFileExtension() {
        assertEquals("csv", strategy.getFileExtension());
    }
}
