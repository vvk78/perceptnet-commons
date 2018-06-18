package com.perceptnet.commons.json;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.perceptnet.api.ItemsLoadService;
import com.perceptnet.api.ItemsSaveService;
import com.perceptnet.commons.utils.FileUtils;
import com.perceptnet.commons.utils.IoUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
public class JsonService implements ItemsLoadService, ItemsSaveService {
    public void saveItem(OutputStream os, Object item) {
        new JsonWriter(os).write(item);
    }

    public void saveItem(String fileName, Object item) {
        OutputStream os = prepareOutput(fileName);
        try {
            JsonWriter jsonWriter = new JsonWriter(os);
            jsonWriter.write(item);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object loadItem(String fileOrResourceName) {
        try (InputStream is = getInput(fileOrResourceName)) {
            if (is instanceof ZipInputStream) {
                ZipEntry entry = ((ZipInputStream)is).getNextEntry();
            }
            Object result = JsonReader.jsonToJava(is, null);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Cannot load class information from " + fileOrResourceName + " due to " + e, e);
        }
    }

    private InputStream getInput(String fileName) throws FileNotFoundException {
        InputStream result;
        if (fileName.startsWith("classpath:")) {
            String resName = fileName.substring("classpath:".length());
            result = getClass().getClassLoader().getResourceAsStream(resName);
            if (result == null) {
                throw new FileNotFoundException("Resource " + resName + " is not found on classpath");
            }
        } else {
            result = new FileInputStream(fileName);
        }
        if (fileName.endsWith(".zip")) {
            result = new ZipInputStream(result);
        }
        return result;
    }

    private OutputStream prepareOutput(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name is not specified");
        }
        FileUtils.prepareFileForReCreation(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            if (fileName.endsWith(".zip")) {
                ZipOutputStream zos = new ZipOutputStream(fos);
                zos.setLevel(9);
                int zipIndex = fileName.lastIndexOf(".zip");
                int simpleNameIndex = fileName.lastIndexOf(".", Math.max(0, zipIndex - 1));
                if (simpleNameIndex == -1) {
                    simpleNameIndex = 0;
                }
                zos.putNextEntry(new ZipEntry(fileName.substring(simpleNameIndex, zipIndex)));
                return zos;
            } else {
                return fos;
            }
        } catch (IOException e) {
            IoUtils.closeSafely(fos);
            throw new RuntimeException(e);
        }
    }

}
