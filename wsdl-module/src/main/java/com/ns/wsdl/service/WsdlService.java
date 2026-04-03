package com.ns.wsdl.service;

import com.ns.wsdl.dto.WsdlRequest;
import com.ns.wsdl.dto.WsdlResponse;

public interface WsdlService {
    WsdlResponse processWsdl(WsdlRequest request);
}
