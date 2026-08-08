package com.competition.platform.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.competition.platform.entity.FileUpload;
import com.competition.platform.mapper.FileUploadMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class CompetitionFileUploadServiceImpl implements CompetitionFileUploadService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${upload.public-url:http://localhost:8090/files/}")
    private String publicUrl;

    private final FileUploadMapper fileUploadMapper;

    public CompetitionFileUploadServiceImpl(FileUploadMapper fileUploadMapper) {
        this.fileUploadMapper = fileUploadMapper;
    }

    public Map<String, Object> upload(MultipartFile file, Long uploaderId, String bizType, Long bizId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件为空");
        }

        String originalName = file.getOriginalFilename();
        String ext = FileUtil.extName(originalName);
        if (ext == null || ext.isEmpty()) {
            ext = "bin";
        }
        String storedName = IdUtil.fastSimpleUUID() + "." + ext;

        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path targetPath = dirPath.resolve(storedName);
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败: " + e.getMessage());
        }

        String filePath = uploadDir + "/" + storedName;

        FileUpload record = new FileUpload();
        record.setOriginalName(originalName);
        record.setStoredName(storedName);
        record.setFilePath(filePath);
        record.setFileSize(file.getSize());
        record.setContentType(file.getContentType());
        record.setUploaderId(uploaderId);
        record.setBizType(bizType);
        record.setBizId(bizId);
        fileUploadMapper.insert(record);

        String url = publicUrl + storedName;

        Map<String, Object> result = new HashMap<>();
        result.put("id", record.getId());
        result.put("url", url);
        result.put("storedName", storedName);
        result.put("originalName", originalName);
        result.put("fileSize", record.getFileSize());
        result.put("filePath", filePath);
        return result;
    }

    public FileUpload getById(Long id) {
        FileUpload record = fileUploadMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("文件记录不存在");
        }
        return record;
    }

    public void delete(Long id) {
        FileUpload record = fileUploadMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("文件记录不存在");
        }
        try {
            Path filePath = Paths.get(record.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
        fileUploadMapper.deleteById(id);
    }
}
