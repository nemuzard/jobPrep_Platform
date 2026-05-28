package com.jobprep.jobprep_platform.service.resumematch.impl;

import com.jobprep.jobprep_platform.service.resumematch.DocumentTextExtractor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TikaDocumentTextExtractor implements DocumentTextExtractor {
    private final AutoDetectParser parser = new AutoDetectParser();

    @Override
    public String extract(InputStream inputStream, String fileName, String contentType) throws IOException {
        Metadata metadata = new Metadata();
        if (fileName != null) {
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);
        }
        if (contentType != null) {
            metadata.set(Metadata.CONTENT_TYPE, contentType);
        }
        BodyContentHandler handler = new BodyContentHandler(-1);
        try {
            parser.parse(inputStream, handler, metadata, new ParseContext());
            return handler.toString().replaceAll("\\s+", " ").trim();
        } catch (TikaException | SAXException e) {
            throw new IOException("Failed to extract resume text", e);
        }
    }
}
