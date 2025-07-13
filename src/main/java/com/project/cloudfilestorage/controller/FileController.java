package com.project.cloudfilestorage.controller;

import com.project.cloudfilestorage.dto.ApiResponse;
import com.project.cloudfilestorage.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file-controller")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /*
    Method to search the files in the S3 bucket.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<String>>> searchFile(@RequestParam String userName, @RequestParam String fileName) {
        List<String> matchedFiles = fileService.searchFile(userName, fileName);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Files found", matchedFiles));
    }

    /*
    Method to download the file from the S3 bucket.
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String userName, @RequestParam String fileName) {
        byte[] fileContent = fileService.downloadFile(userName, fileName);
        return ResponseEntity.status(HttpStatus.OK).body(fileContent);
    }

    /*
    Method to upload the file to the S3 bucket.
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam String userName, @RequestParam("file") MultipartFile file) {
        fileService.uploadFile(userName, file);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("File uploaded successfully"));
    }
}
