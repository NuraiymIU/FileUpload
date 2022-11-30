package com.example.fileupload.file.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@Entity
@Table(name = "FILE_UPLOAD")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String fileOriginalName;
    String url;
    String uuid;


}
