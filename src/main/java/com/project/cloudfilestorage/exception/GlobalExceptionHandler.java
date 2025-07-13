package com.project.cloudfilestorage.exception;

import com.project.cloudfilestorage.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FilesNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleFilesNotFoundException(FilesNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<ApiResponse<String>> handleFileOperationException(FileOperationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Exception occurred."+e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Bad Request. "+e.getMessage()));
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(GenericException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Exception occurred."+e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingRequestParameter(MissingServletRequestParameterException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Bad Request."+e.getMessage()));
    }
}
