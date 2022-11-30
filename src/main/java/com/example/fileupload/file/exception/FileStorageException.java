package com.example.fileupload.file.exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException() {
        super();
    }

    public FileStorageException(String message) {
        super(message);
    }
}
