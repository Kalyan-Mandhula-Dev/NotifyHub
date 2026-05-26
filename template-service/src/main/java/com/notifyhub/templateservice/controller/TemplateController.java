package com.notifyhub.templateservice.controller;

import com.notifyhub.templateservice.dto.request.UploadTemplateRequest;
import com.notifyhub.templateservice.dto.response.TemplateContentResponse;
import com.notifyhub.templateservice.dto.response.TemplateResponse;
import com.notifyhub.templateservice.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TemplateResponse> uploadTemplate(
            @Valid @ModelAttribute UploadTemplateRequest request,
            @RequestPart("file") MultipartFile file) {
        TemplateResponse response = templateService.uploadTemplate(request, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/resolve")
    public ResponseEntity<TemplateContentResponse> resolveTemplate(
            @RequestParam String tenantId,
            @RequestParam String eventType) {
        TemplateContentResponse response =
                templateService.resolveTemplate(tenantId, eventType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<TemplateResponse>> getTemplates(
            @PathVariable String tenantId) {
        return ResponseEntity.ok(
                templateService.getTemplatesByTenant(tenantId)
        );
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<String> deleteTemplate(
            @PathVariable Long templateId) {
        templateService.deleteTemplate(templateId);
        return ResponseEntity.ok("Template deleted successfully");
    }
}
