package org.bkd.saas;

public record ErrorDto(String timestamp, int status, String error, String message, String path) {}
