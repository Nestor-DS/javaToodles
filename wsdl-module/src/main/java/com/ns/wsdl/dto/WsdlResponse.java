package com.ns.wsdl.dto;

public class WsdlResponse {
    private boolean status;
    private String message;
    private String downloadToken;
    private String fileName;
    private int fileCount;

    public WsdlResponse() {}

    public WsdlResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDownloadToken() { return downloadToken; }
    public void setDownloadToken(String downloadToken) { this.downloadToken = downloadToken; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public int getFileCount() { return fileCount; }
    public void setFileCount(int fileCount) { this.fileCount = fileCount; }
}
