package com.example.fileupload.file.service.impl;

import com.example.fileupload.file.entity.FileUpload;
import com.example.fileupload.file.exception.FileStorageException;
import com.example.fileupload.file.repo.FileUploadRepo;
import com.example.fileupload.file.service.FileUploadService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${storage.location}")
    String storageLocation;


     final FileUploadRepo fileUploadRepo;

    public FileUploadServiceImpl(FileUploadRepo fileUploadRepo) {
        this.fileUploadRepo = fileUploadRepo;
    }

    @Override
    public List<String> uploadAndSaveFile(MultipartFile[] multipartFile) {
        List<String> list = new ArrayList<>();
        LocalDate date = LocalDate.now(ZoneId.systemDefault());

        try {

            for (MultipartFile file : multipartFile) {
                Path path = getPath(date);
                String uid =  uuid(date);
                store(file, uid, path);

                FileUpload fileUpload = saveToDataBase(FileUpload.builder()
                        .fileOriginalName(file.getOriginalFilename())
                        .uuid(uid)
                        .url(path + File.separator + uid)
                        .build()
                );
                list.add(fileUpload.getFileOriginalName());

            }

        } catch (Exception e) {
            log.error("", e);
            throw new FileStorageException("Ошибка при загрузке");
        }

        return list;
    }


    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Could not read file: " + filename);
        }
    }

    @Override
    public byte[] downloadFile(String uuid, HttpServletResponse response) {
        try {
            FileUpload fileUpload = getById(uuid);
            Resource resource = loadAsResource(fileUpload.getUrl());
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + fileUpload.getFileOriginalName());
            InputStreamResource inputStreamResource = new InputStreamResource(resource.getInputStream());
            return inputStreamResource.getInputStream().readAllBytes();
        } catch(IOException e) {
            throw new FileStorageException("Failed to download file " + uuid);
        }
    }


    public void store(MultipartFile file, String newFilename, Path path) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file " + filename);
            }

            InputStream inputStream = file.getInputStream();

            Files.copy(inputStream, path.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + filename);
        }
    }

    private String filePathCreate(LocalDate localDate) {
        int year = localDate.getYear();
        int month = localDate.getMonth().getValue();
        int day = localDate.getDayOfMonth();

        String pathStr = String.format("%s/%s/%s/%s", storageLocation, year, month, day);

        try {
            Path path = Paths.get(pathStr);
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new FileStorageException("Failed to create directory");
        }
        return pathStr;
    }

    private String uuid(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedString = localDate.format(formatter);
        String uuid = UUID.randomUUID().toString().substring(0, 16);
        return String.format("%s-%s", formattedString, uuid);
    }

    private Path getPath(LocalDate date) {
        return Paths.get(filePathCreate(date));
    }

    private FileUpload saveToDataBase(FileUpload fileUpload) {
        return fileUploadRepo.save(fileUpload);
    }



    public Path load(String url) {
        return Paths.get(url);
    }

    private FileUpload getById(String uuid){
        return fileUploadRepo.findByUuid(uuid);
    }


}
