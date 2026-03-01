package sigebi.inventory.dto.response;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages
) {}
