package org.nemac.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;

public class TypeDelete {

    public static void recursiveDelete(String dir, String extension) throws IOException {
        String[] extensions = {extension};
        Iterator<File> files = FileUtils.iterateFiles(new File(dir), extensions, true);

        while (files.hasNext()) {
            FileUtils.forceDelete(files.next());
        }
    }
    
}
