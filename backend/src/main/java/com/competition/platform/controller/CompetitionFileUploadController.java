package com.competition.platform.controller;

import com.competition.platform.common.R;
import com.competition.platform.entity.FileUpload;
import com.competition.platform.service.CompetitionFileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/file")
public class CompetitionFileUploadController {

    private final CompetitionFileUploadService fileUploadService;

    public CompetitionFileUploadController(CompetitionFileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    public R<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "bizId", required = false) Long bizId,
                                         @RequestParam(value = "bizType", required = false) String bizType,
                                         HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return R.ok(fileUploadService.upload(file, userId, bizType, bizId));
        } catch (Exception e) {
            log.warn("文件上传失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public R<FileUpload> getById(@PathVariable Long id) {
        try {
            return R.ok(fileUploadService.getById(id));
        } catch (Exception e) {
            log.warn("查询文件失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        try {
            fileUploadService.delete(id);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.warn("删除文件失败: {}", e.getMessage());
            return R.fail(e.getMessage());
        }
    }
}
