package com.project.cloudfilestorage.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<String> searchFiles(String userName, String fileName);

    void uploadFile(String userName, MultipartFile file);

    byte[] downloadFile(String userName, String fileName) ;
}
