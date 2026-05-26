package com.notifyhub.templateservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public void uploadFile(String s3Key, MultipartFile file) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            log.info("Uploaded file to S3: {}/{}", bucket, s3Key);

        } catch (IOException e) {
            log.error("Failed to read file bytes: {}", e.getMessage());
            throw new RuntimeException("Failed to read uploaded file", e);
        } catch (S3Exception e) {
            log.error("S3 upload failed: {}", e.getMessage());
            throw new RuntimeException("Failed to upload to S3: " + e.getMessage(), e);
        }
    }

    public String downloadFile(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            byte bytes[] = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

            String content = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);

            log.info("Downloaded file from S3: {}/{}", bucket, s3Key);
            return content;
        } catch (NoSuchKeyException e) {
            log.error("S3 key not found: {}", s3Key);
            throw new RuntimeException("Template file not found in S3: " + s3Key);
        } catch (S3Exception e) {
            log.error("S3 download failed: {}", e.getMessage());
            throw new RuntimeException("Failed to download from S3: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("Deleted file from S3: {}/{}", bucket, s3Key);

        } catch (S3Exception e) {
            log.error("S3 delete failed: {}", e.getMessage());
            throw new RuntimeException("Failed to delete from S3: " + e.getMessage(), e);
        }
    }
}
