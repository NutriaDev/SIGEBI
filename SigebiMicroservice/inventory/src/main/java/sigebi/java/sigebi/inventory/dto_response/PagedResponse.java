package sigebi.inventory.dto_response;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages
) {}
