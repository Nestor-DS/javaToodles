package com.ns.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompressionException extends RuntimeException {

    private String errorCode;
    private String technicalDetails;

    public CompressionException(String message) {
        super(message);
        this.errorCode = "COMPRESSION_001";
    }

    public CompressionException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "COMPRESSION_002";
    }

    public CompressionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CompressionException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}