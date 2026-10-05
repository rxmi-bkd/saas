package org.bkd.saas.shared.dto;

public record ErrorDto(String timestamp, int status, String error, String message, String path) {}
