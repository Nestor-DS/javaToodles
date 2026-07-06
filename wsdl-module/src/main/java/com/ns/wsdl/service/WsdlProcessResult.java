package com.ns.wsdl.service;

public class WsdlProcessResult {
    private boolean success;
    private String message;
    private byte[] zipContent;
    private int fileCount;

    public WsdlProcessResult(boolean success, String message, byte[] zipContent, int fileCount) {
        this.success = success;
        this.message = message;
        this.zipContent = zipContent;
        this.fileCount = fileCount;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public byte[] getZipContent() { return zipContent; }
    public int getFileCount() { return fileCount; }
}
