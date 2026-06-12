package com.notifyhub.templateservice.service;

import com.notifyhub.templateservice.dto.request.UploadTemplateRequest;
import com.notifyhub.templateservice.dto.response.TemplateContentResponse;
import com.notifyhub.templateservice.dto.response.TemplateResponse;
import com.notifyhub.templateservice.entity.Template;
import com.notifyhub.templateservice.exception.TemplateAlreadyExistsException;
import com.notifyhub.templateservice.exception.TemplateNotFoundException;
import com.notifyhub.templateservice.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final S3Service s3Service;

    @Transactional
    public TemplateResponse uploadTemplate(UploadTemplateRequest request, MultipartFile file) {

        if (templateRepository.existsByTenantIdAndEventType(
                request.getTenantId(), request.getEventType())) {
            throw new TemplateAlreadyExistsException(
                    "Template already exists for eventType: " + request.getEventType() +
                            " and tenant: " + request.getTenantId() +
                            ". Delete the existing one first."
            );
        }

        String s3Key = buildS3Key(
                request.getTenantId(),
                request.getEventType(),
                request.getTemplateName()
        );

        s3Service.uploadFile(s3Key, file);

        // Save metadata to MySQL
        Template template = new Template();
        template.setTenantId(request.getTenantId());
        template.setEventType(request.getEventType());
        template.setTemplateName(request.getTemplateName());
        template.setS3Key(s3Key);

        Template saved = templateRepository.save(template);
        log.info("Template saved. tenantId: {} eventType: {} s3Key: {}",
                request.getTenantId(), request.getEventType(), s3Key);

        return mapToResponse(saved);
    }

    public TemplateContentResponse resolveTemplate(
            String tenantId, String eventType) {

        Template template = templateRepository
                .findByTenantIdAndEventTypeAndIsActive(tenantId, eventType, true)
                .orElseThrow(() -> new TemplateNotFoundException(
                        "No active template found for tenant: " + tenantId +
                                " eventType: " + eventType
                ));

        String content = s3Service.downloadFile(template.getS3Key());

        return TemplateContentResponse.builder()
                .tenantId(tenantId)
                .eventType(eventType)
                .content(content)
                .build();
    }

    public List<TemplateResponse> getTemplatesByTenant(String tenantId) {
        return templateRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTemplate(Long templateId) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException(
                        "Template not found with id: " + templateId
                ));

        s3Service.deleteFile(template.getS3Key());

        templateRepository.deleteById(templateId);
        log.info("Template deleted. id: {} s3Key: {}",
                templateId, template.getS3Key());
    }

    private String buildS3Key(String tenantId, String eventType,
                              String templateName) {
        return tenantId + "/" + eventType + "/" + templateName + ".html";
    }

    private TemplateResponse mapToResponse(Template template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .tenantId(template.getTenantId())
                .eventType(template.getEventType())
                .templateName(template.getTemplateName())
                .s3Key(template.getS3Key())
                .isActive(template.getIsActive())
                .createdAt(template.getCreatedAt())
                .build();
    }
}
