package com.project.cloudfilestorage.dto;

public class ApiResponse<T> {
    private String message;
    private String status;
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiResponse(String message, String status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, "Success", null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, "Error", null);
    }
}
