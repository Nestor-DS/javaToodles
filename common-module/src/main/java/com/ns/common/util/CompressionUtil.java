package com.ns.common.util;

import com.ns.common.constant.CompressionConstants;
import com.ns.common.exception.CompressionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class CompressionUtil {

    /**
     * Crea un archivo ZIP a partir de un mapa de archivos (nombre -> contenido)
     */
    public byte[] createZip(Map<String, byte[]> files) throws CompressionException {
        log.debug("Creando ZIP con {} archivos", files.size());

        // Validaciones usando constantes
        if (files.isEmpty()) {
            throw new CompressionException("No hay archivos para comprimir", "COMPRESSION_100");
        }

        if (files.size() > CompressionConstants.MAX_FILES_IN_ZIP) {
            throw new CompressionException(
                    String.format("Demasiados archivos: %d (máximo: %d)",
                            files.size(), CompressionConstants.MAX_FILES_IN_ZIP),
                    "COMPRESSION_101"
            );
        }

        long totalSize = files.values().stream()
                .mapToLong(arr -> arr.length)
                .sum();

        if (totalSize > CompressionConstants.MAX_ZIP_SIZE) {
            throw new CompressionException(
                    String.format("Tamaño total %d bytes excede el máximo de %d bytes",
                            totalSize, CompressionConstants.MAX_ZIP_SIZE),
                    "COMPRESSION_102"
            );
        }

        // Validar nombres de archivos
        for (String fileName : files.keySet()) {
            if (fileName == null || fileName.isEmpty()) {
                throw new CompressionException("Nombre de archivo vacío o nulo", "COMPRESSION_103");
            }
            if (fileName.length() > CompressionConstants.MAX_FILENAME_LENGTH) {
                throw new CompressionException(
                        String.format("Nombre de archivo demasiado largo: %s (%d caracteres)",
                                fileName, fileName.length()),
                        "COMPRESSION_104"
                );
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            // Usar nivel de compresión desde constantes
            zos.setLevel(CompressionConstants.COMPRESSION_LEVEL_DEFAULT);

            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                String fileName = entry.getKey();
                byte[] content = entry.getValue();

                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                zos.write(content);
                zos.closeEntry();

                log.trace("Agregado archivo al ZIP: {} ({} bytes)", fileName, content.length);
            }

            zos.finish();
            byte[] result = baos.toByteArray();
            log.debug("ZIP creado exitosamente. Tamaño: {} bytes", result.length);
            return result;

        } catch (IOException e) {
            log.error("Error creando archivo ZIP", e);
            throw new CompressionException("Error al comprimir archivos: " + e.getMessage(),
                    "COMPRESSION_999", e);
        }
    }

    /**
     * Comprime una lista de clases Java
     */
    public byte[] compressClasses(List<Class<?>> classes) throws CompressionException {
        log.debug("Comprimiendo {} clases", classes.size());

        if (classes == null || classes.isEmpty()) {
            throw new CompressionException("No hay clases para comprimir", "COMPRESSION_200");
        }

        Map<String, byte[]> files = new HashMap<>();

        for (Class<?> clazz : classes) {
            try {
                String className = clazz.getSimpleName() + CompressionConstants.CLASS_EXTENSION;
                byte[] classBytes = getClassBytes(clazz);
                files.put(className, classBytes);
                log.trace("Clase preparada: {}", className);
            } catch (IOException e) {
                log.error("Error procesando clase: {}", clazz.getName(), e);
                throw new CompressionException(
                        "Error al procesar la clase " + clazz.getName(),
                        "COMPRESSION_201",
                        e
                );
            }
        }

        return createZip(files);
    }

    /**
     * Comprime datos genéricos en un solo archivo
     */
    public byte[] compressData(byte[] data, String entryName) throws CompressionException {
        log.debug("Comprimiendo datos ({} bytes) como: {}", data.length, entryName);

        if (data == null || data.length == 0) {
            throw new CompressionException("No hay datos para comprimir", "COMPRESSION_300");
        }

        if (entryName == null || entryName.isEmpty()) {
            entryName = CompressionConstants.DEFAULT_ENTRY_NAME;
        }

        return createZip(Map.of(entryName, data));
    }

    /**
     * Comprime un solo archivo
     */
    public byte[] compressFile(String fileName, byte[] content) throws CompressionException {
        log.debug("Comprimiendo archivo: {}", fileName);
        return createZip(Map.of(fileName, content));
    }

    /**
     * Obtiene los bytes de una clase
     */
    private byte[] getClassBytes(Class<?> clazz) throws IOException {
        String resourceName = "/" + clazz.getName().replace('.', '/') + ".class";

        try (var is = clazz.getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IOException("No se encontró el archivo .class para: " + clazz.getName());
            }
            return is.readAllBytes();
        }
    }

    /**
     * Crea ZIP con nivel de compresión personalizado
     */
    public byte[] createZipWithLevel(Map<String, byte[]> files, int compressionLevel)
            throws CompressionException {

        if (compressionLevel < CompressionConstants.COMPRESSION_LEVEL_NONE ||
                compressionLevel > CompressionConstants.COMPRESSION_LEVEL_MAX) {
            throw new CompressionException(
                    String.format("Nivel de compresión inválido: %d (debe ser 0-9)", compressionLevel),
                    "COMPRESSION_400"
            );
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            zos.setLevel(compressionLevel);

            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zos.putNextEntry(zipEntry);
                zos.write(entry.getValue());
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error creando ZIP con nivel de compresión", e);
            throw new CompressionException("Error al comprimir: " + e.getMessage(),
                    "COMPRESSION_401", e);
        }
    }
}