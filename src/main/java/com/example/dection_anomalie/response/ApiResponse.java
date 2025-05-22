package com.example.dection_anomalie.response;

public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Réponse en cas de succès
    public static ApiResponse ok(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    // Réponse en cas d'échec
    public static ApiResponse fail(String message) {
        return new ApiResponse(false, message, null);
    }

    // Getters et setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
