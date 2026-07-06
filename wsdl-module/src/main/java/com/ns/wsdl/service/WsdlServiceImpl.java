package com.ns.wsdl.service;

import com.ns.common.util.CompressionUtil;
import com.ns.wsdl.dto.WsdlRequest;
import com.ns.wsdl.util.AxisExecutor;
import com.ns.wsdl.util.DirectoryCleaner;
import com.ns.wsdl.util.WsdlDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class WsdlServiceImpl implements WsdlService {

    private static final Logger log = LoggerFactory.getLogger(WsdlServiceImpl.class);

    private final WsdlDownloader wsdlDownloader;
    private final AxisExecutor axisExecutor;
    private final DirectoryCleaner directoryCleaner;
    private final CompressionUtil compressionUtil;

    public WsdlServiceImpl(WsdlDownloader wsdlDownloader,
                           AxisExecutor axisExecutor,
                           DirectoryCleaner directoryCleaner,
                           CompressionUtil compressionUtil) {
        this.wsdlDownloader = wsdlDownloader;
        this.axisExecutor = axisExecutor;
        this.directoryCleaner = directoryCleaner;
        this.compressionUtil = compressionUtil;
    }

    @Override
    public WsdlProcessResult processWsdl(WsdlRequest request) {
        Path tempDir = null;
        try {
            if (request.getWsdlUrl() == null || request.getWsdlUrl().isBlank()) {
                return new WsdlProcessResult(false, "URL WSDL requerida", null, 0);
            }

            tempDir = Files.createTempDirectory("wsdl-");
            Path srcDir = tempDir.resolve("src");
            Files.createDirectories(srcDir);

            Path wsdlFile = tempDir.resolve("Service.wsdl");
            wsdlDownloader.download(request.getWsdlUrl(), wsdlFile);

            axisExecutor.execute(wsdlFile.toString(), srcDir.toString());

            directoryCleaner.flattenAndClean(srcDir);

            List<Path> javaFiles;
            try (Stream<Path> stream = Files.walk(srcDir)) {
                javaFiles = stream.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".java"))
                        .toList();
            }

            if (javaFiles.isEmpty()) {
                return new WsdlProcessResult(false,
                        "Axis2 no generó clases. Verifica el WSDL.", null, 0);
            }

            Map<String, byte[]> fileMap = new LinkedHashMap<>();
            for (Path file : javaFiles) {
                fileMap.put(file.getFileName().toString(), Files.readAllBytes(file));
            }

            byte[] zipContent = compressionUtil.createZip(fileMap);
            return new WsdlProcessResult(true,
                    "Generadas " + javaFiles.size() + " clases", zipContent, javaFiles.size());

        } catch (Exception e) {
            log.error("Error processing WSDL", e);
            return new WsdlProcessResult(false,
                    "Error: " + e.getMessage(), null, 0);
        } finally {
            if (tempDir != null) {
                directoryCleaner.deleteDirectory(tempDir);
            }
        }
    }
}
