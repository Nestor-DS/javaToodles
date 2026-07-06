package com.ns.wsdl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class WsdlDownloader {

    private static final Logger log = LoggerFactory.getLogger(WsdlDownloader.class);

    public Path download(String wsdlUrl, Path target) throws Exception {
        log.debug("Downloading WSDL from: {}", wsdlUrl);
        URI uri = new URI(wsdlUrl);
        try (InputStream in = uri.toURL().openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        log.debug("WSDL downloaded to: {}", target);
        return target;
    }
}
