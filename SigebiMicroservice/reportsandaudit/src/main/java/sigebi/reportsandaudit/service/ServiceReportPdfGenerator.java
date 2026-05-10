package sigebi.reportsandaudit.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ServiceReportPdfGenerator {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(0, 51, 102));
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 51, 102));
    private static final Font LABEL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font VALUE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);

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
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();

            addTitle(document);
            addEmptyLine(document, 1);
            addMaintenanceInfo(document, maintenanceId, technicianId, reportDate);
            addEmptyLine(document, 1);
            addSection(document, "Diagnóstico", diagnosis);
            addEmptyLine(document, 1);
            addSection(document, "Actividades Realizadas", activitiesPerformed);
            addEmptyLine(document, 1);
            addSection(document, "Observaciones", observations);
            addEmptyLine(document, 1);
            addSection(document, "Repuestos Utilizados", sparePartsUsed);
            addEmptyLine(document, 2);
            addSignatureSpace(document);
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF del reporte técnico", e);
        }
    }

    private void addTitle(Document document) throws DocumentException {
        Paragraph title = new Paragraph("Reporte Técnico de Servicio", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
    }

    private void addMaintenanceInfo(Document document, Long maintenanceId, Long technicianId, LocalDateTime date) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);

        addInfoRow(table, "ID Mantenimiento:", String.valueOf(maintenanceId));
        addInfoRow(table, "Técnico ID:", String.valueOf(technicianId));
        addInfoRow(table, "Fecha del Reporte:", date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        document.add(table);
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        table.addCell(new Phrase(label, LABEL_FONT));
        table.addCell(new Phrase(value != null ? value : "N/A", VALUE_FONT));
    }

    private void addSection(Document document, String title, String content) throws DocumentException {
        Paragraph sectionTitle = new Paragraph(title, SUBTITLE_FONT);
        sectionTitle.setSpacingBefore(5);
        sectionTitle.setSpacingAfter(5);
        document.add(sectionTitle);

        Paragraph sectionContent = new Paragraph(content != null && !content.isBlank() ? content : "N/A", NORMAL_FONT);
        sectionContent.setSpacingAfter(5);
        document.add(sectionContent);
    }

    private void addSignatureSpace(Document document) throws DocumentException {
        Paragraph signatureLabel = new Paragraph("Firma Digital", SUBTITLE_FONT);
        signatureLabel.setSpacingBefore(10);
        document.add(signatureLabel);

        Paragraph signatureBox = new Paragraph("_________________________________________", NORMAL_FONT);
        signatureBox.setSpacingAfter(5);
        document.add(signatureBox);

        Paragraph signatureHint = new Paragraph("Espacio reservado para firma digital", FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY));
        document.add(signatureHint);
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph(
                "Documento generado el " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY)
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private void addEmptyLine(Document document, int count) throws DocumentException {
        for (int i = 0; i < count; i++) {
            document.add(new Paragraph(" "));
        }
    }
}
