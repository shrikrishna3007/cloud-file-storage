package com.project.cloudfilestorage.exception;

public class FilesNotFoundException extends RuntimeException{
    public FilesNotFoundException(String message) {
        super(message);
    }
}
