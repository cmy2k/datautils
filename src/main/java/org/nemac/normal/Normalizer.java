package org.nemac.normal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class Normalizer {
    
    private static final String lineFormat = "%s,%s";
    
    public static List<String> normalize(File input) throws IOException {
        List<String> inLines = FileUtils.readLines(input);
        List<String> outLines = new ArrayList<String>();
        
        for (String line : inLines) {
            String[] elems = line.split(",");
            int date = Integer.parseInt(elems[0]) * 100 + 1;
            for (int i = 1; i < elems.length; i++) {
                outLines.add(String.format(lineFormat, date, elems[i]));
                date++;
            }
        }
        
        return outLines;
    }
}
