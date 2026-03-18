package sigebi.cms.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sigebi.cms.DTO.ImageResponse;
import sigebi.cms.entities.EquipmentImageEntity;
import sigebi.cms.exception.ResourceNotFoundException;
import sigebi.cms.repository.EquipmentImageRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final Cloudinary cloudinary;
    private final EquipmentImageRepository imageRepository;

    public ImageResponse uploadImage(Long equipmentId, MultipartFile file) throws IOException {

        imageRepository.findByEquipmentId(equipmentId).ifPresent(existing -> {
            try {
                cloudinary.uploader().destroy(existing.getPublicId(), ObjectUtils.emptyMap());
                imageRepository.delete(existing);
            } catch (IOException e) {
                log.error("Error eliminando imagen anterior: {}", e.getMessage());
            }
        });

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder",     "sigebi/equipments",
                        "public_id",  "equipment_" + equipmentId,
                        "overwrite",  true
                )
        );

        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        EquipmentImageEntity entity = EquipmentImageEntity.builder()
                .equipmentId(equipmentId)
                .imageUrl(imageUrl)
                .publicId(publicId)
                .uploadedAt(LocalDateTime.now())
                .build();

        imageRepository.save(entity);

        return new ImageResponse(equipmentId, imageUrl, publicId);
    }

    public ImageResponse getImage(Long equipmentId) {
        return imageRepository.findByEquipmentId(equipmentId)
                .map(e -> new ImageResponse(e.getEquipmentId(), e.getImageUrl(), e.getPublicId()))
                .orElseThrow(() -> new ResourceNotFoundException("No hay imagen para el equipo: " + equipmentId));
    }

    public void deleteImage(Long equipmentId) throws IOException {
        EquipmentImageEntity entity = imageRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No hay imagen para el equipo: " + equipmentId));

        cloudinary.uploader().destroy(entity.getPublicId(), ObjectUtils.emptyMap());
        imageRepository.delete(entity);
    }
}