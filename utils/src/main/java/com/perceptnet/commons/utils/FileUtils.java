package com.perceptnet.commons.utils;

import java.io.File;
import java.io.IOException;

/**
 * created by vkorovkin on 05.06.2018
 */
public class FileUtils {

    public static File prepareFileForReCreation(String fileName) {
        File f = new File(fileName);
        if (f.exists() && f.isFile()) {
            f.delete();
        } else if (f.exists() && f.isDirectory()) {
            throw new RuntimeException(fileName + " points to existing directory");
        }
        if (!f.isDirectory()) {
            f.getParentFile().mkdirs();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create " + f.getAbsolutePath() + " due to " + e, e);
        }
        return f;
    }
}
