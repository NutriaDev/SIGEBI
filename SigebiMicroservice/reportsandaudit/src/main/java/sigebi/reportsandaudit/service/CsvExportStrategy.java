package sigebi.reportsandaudit.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component("CSV")
public class CsvExportStrategy implements ReportExportStrategy {

    @Override
    public byte[] export(List<String> headers, List<List<String>> rows) {
        if (rows.isEmpty()) {
            return new byte[0];
        }

        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", headers)).append("\n");

        for (List<String> row : rows) {
            csv.append(String.join(",", row)).append("\n");
        }

        return csv.toString().getBytes();
    }

    @Override
    public String getContentType() {
        return "text/csv";
    }

    @Override
    public String getFileExtension() {
        return "csv";
    }
}
