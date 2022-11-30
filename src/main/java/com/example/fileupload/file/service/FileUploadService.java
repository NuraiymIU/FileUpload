package com.example.fileupload.file.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {
    List<String> uploadAndSaveFile(MultipartFile[] multipartFile);

    Resource loadAsResource(String uuid);

    byte[] downloadFile(String uuid, HttpServletResponse response);
}
