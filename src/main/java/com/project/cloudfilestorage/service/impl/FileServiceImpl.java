package com.project.cloudfilestorage.service.impl;

import com.project.cloudfilestorage.exception.BadRequestException;
import com.project.cloudfilestorage.exception.FileOperationException;
import com.project.cloudfilestorage.exception.FilesNotFoundException;
import com.project.cloudfilestorage.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {
    @Value("${s3.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;
    public FileServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /*
    Search file in S3 bucket. Validate username and file name. If Found then give success response. If not then throw exception.
     */
    @Override
    public List<String> searchFiles(String userName, String fileName) {
        if (isInValid(userName) || isInValid(fileName)) {
            throw new BadRequestException("Invalid userName or fileName");
        }
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(userName + "/")
                .build();

        ListObjectsV2Response response= s3Client.listObjectsV2(request);
        List<String> files = response.contents().stream()
                .map(S3Object::key)
                .filter(key -> key.contains(fileName))
                .map(key-> key.replace(userName + "/", ""))
                .collect(Collectors.toList());

        if (files.isEmpty()){
            throw new FilesNotFoundException("Files not found");
        }
        return files;
    }

    /*
    Helper method to validate username and file name.
     */
    private boolean isInValid(String data) {
        return data == null || data.trim().isEmpty();
    }

    /*
    Upload file in S3 bucket. Validate username and file before upload. If upload done then give success response. If not throw exception.
     */
    @Override
    public void uploadFile(String userName, MultipartFile file) {
        if (isInValid(userName) || file == null || file.isEmpty()) {
            throw new BadRequestException("Invalid user name or file");
        }
        try{
            String key = userName + "/" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (IOException e) {
            throw new FileOperationException("Failed to upload file");
        }
    }

    /*
    Download file from S3 bucket. Validate username and file name before download. If download done then give success response. If not throw exception.
     */
    @Override
    public byte[] downloadFile(String userName, String fileName) {
        if (isInValid(userName) || isInValid(fileName)) {
            throw new BadRequestException("Invalid userName or fileName");
        }
        String key = userName + "/" + fileName;

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try{
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
            return response.readAllBytes();
        }catch (NoSuchKeyException e) {
            throw new FilesNotFoundException("Files not found");
        }catch (IOException e) {
            throw new FileOperationException("Failed to download file");
        }
    }
}
