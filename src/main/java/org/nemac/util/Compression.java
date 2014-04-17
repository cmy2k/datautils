package org.nemac.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.FileUtils;

public class Compression {

    private static final String COMPRESSION_EXTENSION = ".gz";

    public static String compress(String sourceFileName, boolean clean) throws IOException {
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

        if (clean) {
            // cleanup source file
            FileUtils.forceDelete(sourceFile);
        }

        return outputFileName;
    }
    
    public static String uncompress(String sourceFileName, boolean cleanup) throws IOException {
        int suffixDelimiterPosition = sourceFileName.lastIndexOf(COMPRESSION_EXTENSION);
        String outputFileName = sourceFileName.substring(0, suffixDelimiterPosition);

        File sourceFile = new File(sourceFileName);
        GZIPInputStream inputZip = new GZIPInputStream(new FileInputStream(sourceFile));
        FileOutputStream outputFile = new FileOutputStream(outputFileName);
        
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputZip.read(buffer)) > 0) {
            outputFile.write(buffer, 0, len);
        }

        inputZip.close();
        outputFile.close();

        // cleanup source zip
        if (cleanup) {
            FileUtils.forceDelete(sourceFile);
        }

        return outputFileName;
    }
}
