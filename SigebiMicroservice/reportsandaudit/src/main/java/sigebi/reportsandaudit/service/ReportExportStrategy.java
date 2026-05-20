package sigebi.reportsandaudit.service;

import java.util.List;

public interface ReportExportStrategy {

    byte[] export(List<String> headers, List<List<String>> rows);

    String getContentType();

    String getFileExtension();
}
