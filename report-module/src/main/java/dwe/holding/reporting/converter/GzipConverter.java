package dwe.holding.reporting.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Converter
public class GzipConverter implements AttributeConverter<String, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(attribute.getBytes(StandardCharsets.UTF_8));
            gzipStream.finish();
            return byteStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress content", e);
        }
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        if (dbData == null || dbData.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(dbData); GZIPInputStream gzipStream = new GZIPInputStream(byteStream); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress content", e);
        }
    }
}