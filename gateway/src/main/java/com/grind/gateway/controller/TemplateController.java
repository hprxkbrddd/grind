package com.grind.gateway.controller;

import com.grind.gateway.dto.Body;
import com.grind.gateway.dto.template.CreateTrackTemplateDTO;
import com.grind.gateway.dto.template.EditTrackTemplateDTO;
import com.grind.gateway.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/template")
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping("/{trackTemplateId}")
    public ResponseEntity<?> getTemplate(@PathVariable String trackTemplateId) {
        Body<?> body = templateService.callGetTemplate(trackTemplateId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/public")
    public ResponseEntity<?> getPublicTemplates() {
        Body<?> body = templateService.callGetPublicTemplates();
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getTemplatesOfAuthor(@PathVariable String authorId) {
        Body<?> body = templateService.callGetTemplatesOfAuthor(authorId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getTemplatesOfCategory(@PathVariable String categoryId) {
        Body<?> body = templateService.callGetTemplatesOfCategory(categoryId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{trackTemplateId}/archive")
    public ResponseEntity<?> archiveTemplate(@PathVariable String trackTemplateId) {
        Body<?> body = templateService.callArchiveTemplate(trackTemplateId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{trackTemplateId}/publish")
    public ResponseEntity<?> publishTemplate(@PathVariable String trackTemplateId) {
        Body<?> body = templateService.callPublishTemplate(trackTemplateId);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PostMapping
    public ResponseEntity<?> createTemplate(@RequestBody CreateTrackTemplateDTO dto) {
        Body<?> body = templateService.callCreateTemplate(dto);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editTrackTemplate(@PathVariable String id, @RequestBody EditTrackTemplateDTO dto) {
        dto.setId(id);
        Body<?> body = templateService.callEditTemplate(dto);
        return ResponseEntity.status(body.status())
                .body(body.error() == null ?
                        body.payload() : body.error()
                );
    }
}
