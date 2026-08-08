package com.competition.platform.service;

import com.competition.platform.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CompetitionFileUploadService {

    Map<String, Object> upload(MultipartFile file, Long uploaderId, String bizType, Long bizId);

    FileUpload getById(Long id);

    void delete(Long id);
}
