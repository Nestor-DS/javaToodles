package com.ns.wsdl.service;

import com.ns.wsdl.dto.WsdlRequest;

public interface WsdlService {
    WsdlProcessResult processWsdl(WsdlRequest request);
}
