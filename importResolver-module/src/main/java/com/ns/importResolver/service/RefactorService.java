package com.ns.importResolver.service;

import com.ns.importResolver.dto.RefactorRequest;
import java.io.IOException;

public interface RefactorService {
    byte[] refactorAndZip(RefactorRequest request) throws IOException;
}