package com.example.fileupload.file.repo;

import com.example.fileupload.file.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepo extends JpaRepository<FileUpload, Long> {

    FileUpload findByUuid(String uuid);

}
