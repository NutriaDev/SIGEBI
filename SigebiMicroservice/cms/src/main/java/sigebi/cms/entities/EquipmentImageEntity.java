package sigebi.cms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long equipmentId;       // referencia al equipo en el otro MS

    @Column(nullable = false)
    private String imageUrl;        // URL pública de Cloudinary

    @Column(nullable = false)
    private String publicId;        // ID de Cloudinary para poder eliminar

    private LocalDateTime uploadedAt;
}
