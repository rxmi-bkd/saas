package org.bkd.saas;

public record DefaultErrorResponse(
    String timestamp, int status, String error, String message, String path) {}
