package sigebi.reportsandaudit.dto_request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditFilterRequest {

    private Long userId;

    private String module;

    private String action;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    private int page = 0;

    private int size = 10;
}
