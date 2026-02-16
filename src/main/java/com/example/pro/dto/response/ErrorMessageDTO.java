package com.example.pro.dto.response;

/**
 * Customize an error message in case of error
 * Format :
 * {
 * "code": "business code",
 * "message": "user-friendly message"
 * }
 */
public record ErrorMessageDTO(String code, String message) {
}
