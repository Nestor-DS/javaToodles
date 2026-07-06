package com.ns.wsdl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class AxisExecutor {

    private static final Logger log = LoggerFactory.getLogger(AxisExecutor.class);

    public void execute(String wsdlFile, String outputDir) throws Exception {
        log.debug("Running Axis2 WSDL2Java: wsdl={}, output={}", wsdlFile, outputDir);

        String classpath = System.getProperty("java.class.path");

        ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", classpath,
                "org.apache.axis2.wsdl.WSDL2Java",
                "-uri", wsdlFile,
                "-o", outputDir,
                "-d", "adb",
                "-s", "-u"
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("[Axis2] {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Axis2 terminated with error. Exit code: " + exitCode);
        }

        log.debug("Axis2 completed successfully");
    }
}
