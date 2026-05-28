package com.jobprep.jobprep_platform.service.resumematch;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentTextExtractor {
    String extract(InputStream inputStream, String fileName, String contentType) throws IOException;
}
