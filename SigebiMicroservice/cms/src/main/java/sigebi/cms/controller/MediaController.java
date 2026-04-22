package sigebi.cms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sigebi.cms.DTO.ImageResponse;
import sigebi.cms.service.MediaService;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/equipment/{equipmentId}")
    public ResponseEntity<ImageResponse> uploadImage(
            @PathVariable Long equipmentId,
            @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(mediaService.uploadImage(equipmentId, file));
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<ImageResponse> getImage(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(mediaService.getImage(equipmentId));
    }

    @DeleteMapping("/equipment/{equipmentId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long equipmentId) throws IOException {
        mediaService.deleteImage(equipmentId);
        return ResponseEntity.ok().build();
    }
}