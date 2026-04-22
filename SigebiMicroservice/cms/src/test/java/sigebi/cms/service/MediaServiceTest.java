package sigebi.cms.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import sigebi.cms.DTO.ImageResponse;
import sigebi.cms.clients.EquipmentClient;
import sigebi.cms.entities.EquipmentImageEntity;
import sigebi.cms.exception.ResourceNotFoundException;
import sigebi.cms.repository.EquipmentImageRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MediaService - Pruebas Unitarias")
class MediaServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private EquipmentImageRepository imageRepository;

    @Mock
    private EquipmentClient equipmentClient;

    @InjectMocks
    private MediaService mediaService;

    private final Long EQUIPMENT_ID = 8L;
    private final String IMAGE_URL = "https://res.cloudinary.com/dzf0no9gx/image/upload/sigebi/equipments/equipment_8.jpg";
    private final String PUBLIC_ID = "sigebi/equipments/equipment_8";

    private EquipmentImageEntity imageEntity;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        imageEntity = EquipmentImageEntity.builder()
                .id(1L)
                .equipmentId(EQUIPMENT_ID)
                .imageUrl(IMAGE_URL)
                .publicId(PUBLIC_ID)
                .uploadedAt(LocalDateTime.now())
                .build();

        mockFile = new MockMultipartFile(
                "file",
                "equipo.jpg",
                "image/jpeg",
                "imagen-test".getBytes()
        );
    }

    // ══════════════════════════════════════════════════════
    // uploadImage
    // ══════════════════════════════════════════════════════

    @Test
    @DisplayName("uploadImage - debe subir imagen exitosamente cuando el equipo existe y no tiene imagen previa")
    void uploadImage_shouldUploadSuccessfully_whenEquipmentExistsAndNoExistingImage() throws IOException {
        // Arrange
        when(equipmentClient.getEquipmentById(EQUIPMENT_ID)).thenReturn(new Object());
        when(imageRepository.findByEquipmentId(EQUIPMENT_ID)).thenReturn(Optional.empty());
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of(
                "secure_url", IMAGE_URL,
                "public_id", PUBLIC_ID
        ));
        when(imageRepository.save(any(EquipmentImageEntity.class))).thenReturn(imageEntity);

        // Act
        ImageResponse response = mediaService.uploadImage(EQUIPMENT_ID, mockFile);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEquipmentId()).isEqualTo(EQUIPMENT_ID);
        assertThat(response.getImageUrl()).isEqualTo(IMAGE_URL);
        assertThat(response.getPublicId()).isEqualTo(PUBLIC_ID);

        verify(imageRepository).save(any(EquipmentImageEntity.class));
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    @DisplayName("uploadImage - debe eliminar imagen anterior antes de subir la nueva")
    void uploadImage_shouldDeletePreviousImage_whenEquipmentAlreadyHasImage() throws IOException {
        // Arrange
        when(equipmentClient.getEquipmentById(EQUIPMENT_ID)).thenReturn(new Object());
        when(imageRepository.findByEquipmentId(EQUIPMENT_ID)).thenReturn(Optional.of(imageEntity));
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenReturn(Map.of("result", "ok"));
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of(
                "secure_url", IMAGE_URL,
                "public_id", PUBLIC_ID
        ));
        when(imageRepository.save(any(EquipmentImageEntity.class))).thenReturn(imageEntity);

        // Act
        ImageResponse response = mediaService.uploadImage(EQUIPMENT_ID, mockFile);

        // Assert
        assertThat(response).isNotNull();
        verify(uploader).destroy(eq(PUBLIC_ID), anyMap());
        verify(imageRepository).delete(imageEntity);
        verify(imageRepository).save(any(EquipmentImageEntity.class));
    }

    @Test
    @DisplayName("uploadImage - debe lanzar ResourceNotFoundException cuando el equipo no existe")
    void uploadImage_shouldThrowResourceNotFoundException_whenEquipmentNotFound() {
        // Arrange
        when(equipmentClient.getEquipmentById(EQUIPMENT_ID))
                .thenThrow(new RuntimeException("404 Not Found"));

        // Act & Assert
        assertThatThrownBy(() -> mediaService.uploadImage(EQUIPMENT_ID, mockFile))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Equipo no encontrado con ID: " + EQUIPMENT_ID);

        verify(imageRepository, never()).save(any());
        verify(imageRepository, never()).findByEquipmentId(any());
    }

    // ══════════════════════════════════════════════════════
    // getImage
    // ══════════════════════════════════════════════════════

    @Test
    @DisplayName("getImage - debe retornar ImageResponse cuando existe imagen para el equipo")
    void getImage_shouldReturnImageResponse_whenImageExists() {
        // Arrange
        when(imageRepository.findByEquipmentId(EQUIPMENT_ID)).thenReturn(Optional.of(imageEntity));

        // Act
        ImageResponse response = mediaService.getImage(EQUIPMENT_ID);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEquipmentId()).isEqualTo(EQUIPMENT_ID);
        assertThat(response.getImageUrl()).isEqualTo(IMAGE_URL);
        assertThat(response.getPublicId()).isEqualTo(PUBLIC_ID);

        verify(imageRepository).findByEquipmentId(EQUIPMENT_ID);
    }

    @Test
    @DisplayName("getImage - debe lanzar ResourceNotFoundException cuando no existe imagen para el equipo")
    void getImage_shouldThrowResourceNotFoundException_whenImageNotFound() {
        // Arrange
        when(imageRepository.findByEquipmentId(EQUIPMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mediaService.getImage(EQUIPMENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hay imagen para el equipo: " + EQUIPMENT_ID);

        verify(imageRepository).findByEquipmentId(EQUIPMENT_ID);
    }

    // ══════════════════════════════════════════════════════
    // deleteImage
    // ══════════════════════════════════════════════════════

    @Test
    @DisplayName("deleteImage - debe eliminar imagen de Cloudinary y de la BD exitosamente")
    void deleteImage_shouldDeleteFromCloudinaryAndDatabase_whenImageExists() throws IOException {
        // Arrange
        when(imageRepository.findByEquipmentId(EQUIPMENT_ID)).thenReturn(Optional.of(imageEntity));
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenReturn(Map.of("result", "ok"));

        // Act
        mediaService.deleteImage(EQUIPMENT_ID);

        // Assert
        verify(uploader).destroy(eq(PUBLIC_ID), anyMap());
        verify(imageRepository).delete(imageEntity);
    }

    @Test
    @DisplayName("deleteImage - debe lanzar ResourceNotFoundException cuando no existe imagen para el equipo")
    void deleteImage_shouldThrowResourceNotFoundException_whenImageNotFound() {
        // Arrange
        when(imageRepository.findByEquipmentId(EQUIPMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mediaService.deleteImage(EQUIPMENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hay imagen para el equipo: " + EQUIPMENT_ID);

        verify(imageRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteImage - debe propagar IOException si Cloudinary falla al eliminar")
    void deleteImage_shouldPropagateIOException_whenCloudinaryFails() throws IOException {
        // Arrange
        when(imageRepository.findByEquipmentId(EQUIPMENT_ID)).thenReturn(Optional.of(imageEntity));
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new IOException("Cloudinary error"));

        // Act & Assert
        assertThatThrownBy(() -> mediaService.deleteImage(EQUIPMENT_ID))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Cloudinary error");

        verify(imageRepository, never()).delete(any());
    }
}