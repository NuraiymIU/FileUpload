package com.example.fileupload.file.controller;

import com.example.fileupload.file.service.FileUploadService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileUploadService fileUploadService;
    private final Environment environment;

    public FileController(FileUploadService fileUploadService, Environment environment) {
        this.fileUploadService = fileUploadService;
        this.environment = environment;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFile(@RequestParam("files") MultipartFile[] multipartFile) {
        Arrays.stream(environment.getActiveProfiles()).forEach(System.out::println);
        return new ResponseEntity<>(
                fileUploadService.uploadAndSaveFile(multipartFile),
                HttpStatus.OK
        );
    }

    @GetMapping("/{uid}")
    public HttpEntity<byte[]> getFile(@PathVariable String uid, HttpServletResponse response) {
        return new HttpEntity<>(fileUploadService.downloadFile(uid, response));
    }

}
