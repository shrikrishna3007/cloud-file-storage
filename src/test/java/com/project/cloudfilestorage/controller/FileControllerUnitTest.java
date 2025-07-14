package com.project.cloudfilestorage.controller;

import com.project.cloudfilestorage.dto.ApiResponse;
import com.project.cloudfilestorage.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/*
Unit Test for controller class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("File Controller Unit Test")
class FileControllerUnitTest {
    @Mock
    private FileService fileService;
    @InjectMocks
    private FileController fileController;

    @Test
    @DisplayName("Search Files Test")
    void searchFiles_Test() {
        // Arrange
        List<String> mockFiles = List.of("file1", "file2");
        when(fileService.searchFiles("username", "file")).thenReturn(mockFiles);

        // Act
        ResponseEntity<ApiResponse<List<String>>> response = fileController.searchFiles("username", "file");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Files found", response.getBody().getMessage());
        assertEquals(mockFiles, response.getBody().getData());
    }

    @Test
    @DisplayName("Download File Test")
    void downloadFile_Test() {
        // Arrange
        byte[] fileContent = "file content".getBytes();
        when(fileService.downloadFile("username", "file")).thenReturn(fileContent);
        // Act
        ResponseEntity<byte[]> response = fileController.downloadFile("username", "file");
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(fileContent, response.getBody());
    }

    @Test
    @DisplayName("Upload File Test")
    void uploadFile_Test() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "file content".getBytes());
        doNothing().when(fileService).uploadFile("username", file);
        // Act
        ResponseEntity<ApiResponse<String>> response = fileController.uploadFile("username", file);
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("File uploaded successfully", response.getBody().getMessage());
    }
}
