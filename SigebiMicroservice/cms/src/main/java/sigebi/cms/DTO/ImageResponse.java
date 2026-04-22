package sigebi.cms.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {

    private Long equipmentId;
    private String imageUrl;
    private String publicId;
}
