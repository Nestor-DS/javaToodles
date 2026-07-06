package com.ns.wsdl.dto;

import jakarta.validation.constraints.NotBlank;

public class WsdlRequest {

    @NotBlank(message = "La URL del WSDL es requerida")
    private String wsdlUrl;

    private String destination;

    public WsdlRequest() {
    }

    public WsdlRequest(String wsdlUrl, String destination) {
        this.wsdlUrl = wsdlUrl;
        this.destination = destination;
    }

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "WsdlRequest{" +
                "wsdlUrl='" + wsdlUrl + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
