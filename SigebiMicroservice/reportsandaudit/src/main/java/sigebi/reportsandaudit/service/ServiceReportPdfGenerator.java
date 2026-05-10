package sigebi.reportsandaudit.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ServiceReportPdfGenerator {

    // ── Colores ──────────────────────────────────────────────────────────────
    private static final Color BRAND_BLUE  = new Color(38, 61, 66);   // #263d42
    private static final Color WHITE       = Color.WHITE;
    private static final Color LIGHT_GRAY  = new Color(220, 220, 220);
    private static final Color BLACK       = Color.BLACK;

    // ── Fuentes ──────────────────────────────────────────────────────────────
    private static final Font TITLE_FONT        = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BRAND_BLUE);
    private static final Font SECTION_FONT      = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,  WHITE);
    private static final Font LABEL_FONT        = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8,  BLACK);
    private static final Font VALUE_FONT        = FontFactory.getFont(FontFactory.HELVETICA,      9,  BLACK);
    private static final Font COL_HEADER_FONT   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8,  BLACK);
    private static final Font FOOTER_FONT       = FontFactory.getFont(FontFactory.HELVETICA,      7,  Color.GRAY);

    // ── Altura de filas de contenido (en puntos) ──────────────────────────────
    private static final float CONTENT_ROW_HEIGHT = 18f;
    private static final float TALL_ROW_HEIGHT    = 55f;

    // =========================================================================
    public byte[] generate(
            Long maintenanceId,
            String diagnosis,
            String activitiesPerformed,
            String observations,
            String sparePartsUsed,
            Long technicianId,
            LocalDateTime reportDate
    ) {
        try {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            // 1. Encabezado del documento
            addDocumentHeader(document, maintenanceId, technicianId, reportDate);

            // 2. Quién reporta
            addWhoReports(document);

            // 3. Secciones de contenido
            addSectionBlock(document, "DAIGNOSTICO",          diagnosis,           TALL_ROW_HEIGHT);
            addSectionBlock(document, "ACTIVIDADES REALIZADAS", activitiesPerformed, TALL_ROW_HEIGHT);
            addSectionBlock(document, "OBSERVACIONES",         observations,        TALL_ROW_HEIGHT);

            // 4. Tabla de repuestos
            addSparePartsTable(document, sparePartsUsed);

            // 5. Firmas
            addSignatureSection(document);

            // 6. Footer
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF del reporte técnico", e);
        }
    }

    // ── 1. Encabezado ─────────────────────────────────────────────────────────
    private void addDocumentHeader(Document doc, Long maintenanceId, Long technicianId, LocalDateTime date)
            throws DocumentException {

        String dateStr  = date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
        String techStr  = technicianId != null ? String.valueOf(technicianId) : "N/A";
        String maintStr = maintenanceId != null ? String.valueOf(maintenanceId) : "N/A";

        // Título fuera del cuadro, en azul
        Paragraph title = new Paragraph("REPORTE TÉCNICO DE SERVICIO", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(8);
        doc.add(title);

        // Fila de metadatos: FECHA | SEDE | EQUIPO (ID Mant.) | SERIE (Técnico)
        PdfPTable metaTable = new PdfPTable(new float[]{1, 1, 1, 1});
        metaTable.setWidthPercentage(100);
        metaTable.setSpacingAfter(0);

        addMetaCell(metaTable, "FECHA:",   dateStr);
        addMetaCell(metaTable, "SEDE:",    "");
        addMetaCell(metaTable, "EQUIPO:",  "Mant. #" + maintStr);
        addMetaCell(metaTable, "SERIE:",   "Téc. #"  + techStr);

        doc.add(metaTable);
    }

    private void addMetaCell(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BRAND_BLUE);
        cell.setBorderColor(WHITE);
        cell.setBorderWidth(1f);
        cell.setPadding(4);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", SECTION_FONT));
        p.add(new Chunk(value,        FontFactory.getFont(FontFactory.HELVETICA, 8, WHITE)));
        cell.addElement(p);
        table.addCell(cell);
    }

    // ── 2. Quién reporta ──────────────────────────────────────────────────────
    private void addWhoReports(Document doc) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1.5f, 5});
        table.setWidthPercentage(100);
        table.setSpacingBefore(0);
        table.setSpacingAfter(0);

        PdfPCell labelCell = new PdfPCell(new Phrase("QUIEN REPORTA:", SECTION_FONT));
        labelCell.setBackgroundColor(BRAND_BLUE);
        labelCell.setBorderColor(BLACK);
        labelCell.setBorderWidth(0.5f);
        labelCell.setPadding(4);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase("N/A", VALUE_FONT));
        valueCell.setBorderColor(BLACK);
        valueCell.setBorderWidth(0.5f);
        valueCell.setPadding(4);
        table.addCell(valueCell);

        doc.add(table);
    }

    // ── 3. Bloque de sección con encabezado azul y área de contenido ──────────
    private void addSectionBlock(Document doc, String title, String content, float contentHeight)
            throws DocumentException {

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0);
        table.setSpacingAfter(0);

        // Encabezado de sección
        PdfPCell headerCell = new PdfPCell(new Phrase(title, SECTION_FONT));
        headerCell.setBackgroundColor(BRAND_BLUE);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setBorderColor(BLACK);
        headerCell.setBorderWidth(0.5f);
        headerCell.setPadding(4);
        table.addCell(headerCell);

        // Contenido
        String text = (content != null && !content.isBlank()) ? content : "";
        PdfPCell contentCell = new PdfPCell(new Phrase(text, VALUE_FONT));
        contentCell.setMinimumHeight(contentHeight);
        contentCell.setBorderColor(BLACK);
        contentCell.setBorderWidth(0.5f);
        contentCell.setPadding(6);
        contentCell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(contentCell);

        doc.add(table);
    }

    // ── 4. Tabla de repuestos ─────────────────────────────────────────────────
    private void addSparePartsTable(Document doc, String sparePartsUsed) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1, 1.5f, 4});
        table.setWidthPercentage(100);
        table.setSpacingBefore(0);
        table.setSpacingAfter(0);

        // Encabezado de sección (colspan 3)
        PdfPCell sectionHeader = new PdfPCell(new Phrase("REPUESTOS USADOS", SECTION_FONT));
        sectionHeader.setColspan(3);
        sectionHeader.setBackgroundColor(BRAND_BLUE);
        sectionHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        sectionHeader.setBorderColor(BLACK);
        sectionHeader.setBorderWidth(0.5f);
        sectionHeader.setPadding(4);
        table.addCell(sectionHeader);

        // Cabeceras de columna
        addColumnHeader(table, "CANT.");
        addColumnHeader(table, "REF");
        addColumnHeader(table, "DESCRIPCION");

        // Filas de datos — se parsea el string de repuestos si viene separado por comas
        String[] parts = (sparePartsUsed != null && !sparePartsUsed.isBlank())
                ? sparePartsUsed.split(",")
                : new String[0];

        int filledRows = 0;
        for (String part : parts) {
            addEmptySpareRow(table, part.trim());
            filledRows++;
        }

        // Rellenar al menos 5 filas vacías para que el formato se vea completo
        int totalRows = Math.max(5, filledRows + 2);
        for (int i = filledRows; i < totalRows; i++) {
            addEmptySpareRow(table, "");
        }

        doc.add(table);
    }

    private void addColumnHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, COL_HEADER_FONT));
        cell.setBackgroundColor(LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(BLACK);
        cell.setBorderWidth(0.5f);
        cell.setPadding(3);
        table.addCell(cell);
    }

    private void addEmptySpareRow(PdfPTable table, String description) {
        // CANT.
        PdfPCell cantCell = new PdfPCell(new Phrase("", VALUE_FONT));
        cantCell.setMinimumHeight(CONTENT_ROW_HEIGHT);
        cantCell.setBorderColor(BLACK);
        cantCell.setBorderWidth(0.5f);
        cantCell.setPadding(3);
        table.addCell(cantCell);

        // REF
        PdfPCell refCell = new PdfPCell(new Phrase("", VALUE_FONT));
        refCell.setMinimumHeight(CONTENT_ROW_HEIGHT);
        refCell.setBorderColor(BLACK);
        refCell.setBorderWidth(0.5f);
        refCell.setPadding(3);
        table.addCell(refCell);

        // DESCRIPCION
        PdfPCell descCell = new PdfPCell(new Phrase(description, VALUE_FONT));
        descCell.setMinimumHeight(CONTENT_ROW_HEIGHT);
        descCell.setBorderColor(BLACK);
        descCell.setBorderWidth(0.5f);
        descCell.setPadding(3);
        table.addCell(descCell);
    }

    // ── 5. Firmas ─────────────────────────────────────────────────────────────
    private void addSignatureSection(Document doc) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1, 1});
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);
        table.setSpacingAfter(4);

        // Encabezados
        addSignatureHeader(table, "REALIZADO");
        addSignatureHeader(table, "RECIBIDO");

        // Espacio para firma
        addSignatureBody(table, 45f);
        addSignatureBody(table, 45f);

        // NOMBRE
        addSignatureLabel(table, "NOMRE");  // mantiene el typo del template original
        addSignatureLabel(table, "NOMRE");

        addSignatureValue(table, "");
        addSignatureValue(table, "");

        // CARGO
        addSignatureLabel(table, "CARGO");
        addSignatureLabel(table, "CARGO");

        addSignatureValue(table, "");
        addSignatureValue(table, "");

        doc.add(table);
    }

    private void addSignatureHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, SECTION_FONT));
        cell.setBackgroundColor(BRAND_BLUE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(BLACK);
        cell.setBorderWidth(0.5f);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void addSignatureBody(PdfPTable table, float height) {
        PdfPCell cell = new PdfPCell(new Phrase("", VALUE_FONT));
        cell.setMinimumHeight(height);
        cell.setBorderColor(BLACK);
        cell.setBorderWidth(0.5f);
        cell.setPadding(3);
        table.addCell(cell);
    }

    private void addSignatureLabel(PdfPTable table, String label) {
        PdfPCell cell = new PdfPCell(new Phrase(label, LABEL_FONT));
        cell.setBackgroundColor(LIGHT_GRAY);
        cell.setBorderColor(BLACK);
        cell.setBorderWidth(0.5f);
        cell.setPadding(3);
        table.addCell(cell);
    }

    private void addSignatureValue(PdfPTable table, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value, VALUE_FONT));
        cell.setBorderColor(BLACK);
        cell.setBorderWidth(0.5f);
        cell.setPadding(3);
        cell.setMinimumHeight(CONTENT_ROW_HEIGHT);
        table.addCell(cell);
    }

    // ── 6. Footer ─────────────────────────────────────────────────────────────
    private void addFooter(Document doc) throws DocumentException {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        Paragraph footer = new Paragraph("Documento generado el " + ts, FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(4);
        doc.add(footer);
    }
}