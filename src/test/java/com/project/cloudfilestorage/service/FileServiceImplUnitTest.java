package com.project.cloudfilestorage.service;

import com.project.cloudfilestorage.exception.BadRequestException;
import com.project.cloudfilestorage.exception.FileOperationException;
import com.project.cloudfilestorage.exception.FilesNotFoundException;
import com.project.cloudfilestorage.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("File Service Unit Test")
class FileServiceImplUnitTest {
    @Mock
    private S3Client s3Client;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileServiceImpl(s3Client);
        ReflectionTestUtils.setField(fileService, "bucketName", "test-bucket");
    }

    @Nested
    @DisplayName("Search Files Unit Tests")
    class SearchFilesUnitTests{
        @Test
        @DisplayName("Search Files: Success Test")
        void searchFile_SuccessTest() {
            // Arrange
            ListObjectsV2Response mockResponse = ListObjectsV2Response.builder()
                    .contents(S3Object.builder().key("test-user/test-file.txt").build())
                    .build();
            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockResponse);
            // Act
            List<String> files = fileService.searchFiles("test-user", "test-file");
            // Assert
            assertEquals(1, files.size());
            assertEquals("test-file.txt", files.get(0));
        }

        @Test
        @DisplayName("Search Files: Username Null Test")
        void searchFiles_UsernameNullTest() {
            // Assert
            assertThrows(BadRequestException.class, () -> fileService.searchFiles(null, "invalid-file"));
        }

        @Test
        @DisplayName("Search Files: File is Null Test")
        void searchFiles_FileIsNullTest(){
            assertThrows(BadRequestException.class, () -> fileService.searchFiles("invalid-user", null));
        }

        @Test
        @DisplayName("Search Files: Empty File Test")
        void searchFiles_EmptyFileTest(){
            assertThrows(BadRequestException.class,()-> fileService.searchFiles("valid-user", ""));
        }

        @Test
        @DisplayName("Search Files: Empty Username Test")
        void searchFiles_EmptyUsernameTest(){
            assertThrows(BadRequestException.class,()-> fileService.searchFiles("", "valid-file"));
        }

        @Test
        @DisplayName("Search Files: Invalid Username and File Test")
        void searchFiles_InvalidUsernameAndFileTest(){
            assertThrows(BadRequestException.class,()-> fileService.searchFiles(" ", " "));
        }

        @Test
        @DisplayName("Search Files: No Files Found Test")
        void searchFiles_NoFilesFoundTest() {
            ListObjectsV2Response mockResponse = ListObjectsV2Response.builder().contents(List.of()).build();
            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockResponse);
            // Assert
            assertThrows(FilesNotFoundException.class, () -> fileService.searchFiles("test-user", "test-file"));
        }

    }

    @Nested
    @DisplayName("Download File Unit Tests")
    class DownloadFileUnitTests{
        @Test
        @DisplayName("Download File: Success Test")
        void downloadFile_SuccessTest() {
            // Arrange
            byte[] fileContent = "file content".getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
            ResponseInputStream<GetObjectResponse> response = new ResponseInputStream<>(
                    GetObjectResponse.builder().build(),
                    inputStream
            );
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(response);
            // Act
            byte[] downloadedFile = fileService.downloadFile("test-user", "test-file");
            // Assert
            assertArrayEquals(fileContent, downloadedFile);
        }

        @Test
        @DisplayName("Download File: Invalid Input Test")
        void downloadFile_InvalidInputTest() {
            // Assert
            assertThrows(BadRequestException.class,()-> fileService.downloadFile(" ", " "));
        }

        @Test
        @DisplayName("Download File: Username Null Test")
        void downloadFile_UsernameNullTest() {
            // Assert
            assertThrows(BadRequestException.class, () -> fileService.downloadFile(null, "valid-file"));
        }

        @Test
        @DisplayName("Download File: File is Null Test")
        void downloadFile_FileIsNullTest(){
            assertThrows(BadRequestException.class, () -> fileService.downloadFile("valid-user", null));
        }

        @Test
        @DisplayName("Download File: Empty File Test")
        void downloadFile_EmptyFileTest(){
            assertThrows(BadRequestException.class,()-> fileService.downloadFile("valid-user", ""));
        }

        @Test
        @DisplayName("Download File: Empty Username Test")
        void downloadFile_EmptyUsernameTest(){
            assertThrows(BadRequestException.class,()-> fileService.downloadFile("", "valid-file"));
        }

        @Test
        @DisplayName("Download File: File Not Found Test")
        void downloadFile_FileNotFoundTest() {
            // Arrange
            when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());
            // Assert
            assertThrows(FilesNotFoundException.class, () -> fileService.downloadFile("test-user", "test-file"));
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("Download File: IO Exception Test")
        void downloadFile_IOErrorTest() throws IOException {
            // Arrange
            ResponseInputStream<GetObjectResponse> stream = mock(ResponseInputStream.class);
            when(stream.readAllBytes()).thenThrow(new IOException("IO Error"));
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);
            // Assert
            assertThrows(FileOperationException.class, () -> fileService.downloadFile("test-user", "test-file"));
        }
    }


    @Nested
    @DisplayName("Upload File Unit Tests")
    class UploadFileUnitTests{
        /*
        Helper methods to create valid and empty files
         */
        private MockMultipartFile createValidFile() {
            return new MockMultipartFile("file","file.txt","text/plain","content".getBytes());
        }

        private MockMultipartFile createEmptyFile() {
            return new MockMultipartFile("file","","text/plain",new byte[0]);
        }

        @Test
        @DisplayName("Upload File: Success Test")
        void uploadFile_SuccessTest(){
            // Arrange
            when(s3Client.putObject(any(PutObjectRequest.class),any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());
            // Act
            fileService.uploadFile("test-user",createValidFile());
            verify(s3Client,times(1)).putObject(any(PutObjectRequest.class),any(RequestBody.class));
        }

        @Test
        @DisplayName("Upload File: Invalid Input Test")
        void uploadFile_InvalidInputTest(){
            // Assert
            MultipartFile file = createEmptyFile();
            assertThrows(BadRequestException.class,()->fileService.uploadFile(" ",file));
        }

        @Test
        @DisplayName("Upload File: Empty File Test")
        void uploadFile_EmptyFileTest(){
            MultipartFile file = createEmptyFile();
            assertThrows(BadRequestException.class,()->fileService.uploadFile("test-user",file));
        }

        @Test
        @DisplayName("Upload File: Empty Username Test")
        void uploadFile_EmptyUsernameTest(){
            MultipartFile file = createValidFile();
            assertThrows(BadRequestException.class,()->fileService.uploadFile("",file));
        }

        @Test
        @DisplayName("Upload File: File Null Test")
        void uploadFile_FileIsNullTest(){
            assertThrows(BadRequestException.class,()->fileService.uploadFile("test-user",null));
        }

        @Test
        @DisplayName("Upload File: Username Null Test")
        void uploadFile_UsernameNullTest(){
            MultipartFile file = createValidFile();
            assertThrows(BadRequestException.class,()->fileService.uploadFile(null,file));
        }

        @Test
        @DisplayName("Upload File: IO Exception Test")
        void uploadFile_IOErrorTest() throws IOException {
            // Arrange
            MultipartFile mockFile = mock(MultipartFile.class);
            when(mockFile.getOriginalFilename()).thenReturn("test-file");
            when(mockFile.getContentType()).thenReturn("text/plain");

            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getInputStream()).thenThrow(new IOException("IO Error"));

            // Assert
            assertThrows(FileOperationException.class, () -> fileService.uploadFile("test-user", mockFile));
        }
    }
}
