package org.nemac.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.FileUtils;

public class Compression {

    private static final String COMPRESSION_EXTENSION = ".gz";

    public static String compress(String sourceFileName) throws IOException {
        String outputFileName = sourceFileName + COMPRESSION_EXTENSION;

        File sourceFile = new File(sourceFileName);

        FileInputStream in = new FileInputStream(sourceFile);
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outputFileName));

        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();

        // cleanup source file
        FileUtils.forceDelete(sourceFile);

        return outputFileName;
    }
}
