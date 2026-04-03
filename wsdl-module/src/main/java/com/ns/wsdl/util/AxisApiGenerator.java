package com.ns.wsdl.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AxisApiGenerator {

    public static void execute(String wsdlFile, String outputDir) throws Exception {

        String classpath = System.getProperty("java.class.path");

        ProcessBuilder pb = new ProcessBuilder(
                "java",
                "-cp",
                classpath,
                "org.apache.axis2.wsdl.WSDL2Java",
                "-uri",
                wsdlFile,
                "-o",
                outputDir,
                "-d",
                "adb",
                "-s",
                "-u"
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Axis2 terminated with error. Exit code: " + exitCode);
        }
    }

}