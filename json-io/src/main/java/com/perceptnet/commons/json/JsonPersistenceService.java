package com.perceptnet.commons.json;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.perceptnet.api.ItemsLoadService;
import com.perceptnet.api.ItemsSaveService;
import com.perceptnet.commons.utils.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 17.06.2018
 */
public class JsonPersistenceService implements ItemsLoadService, ItemsSaveService {

    private OutputStream os;
    private JsonWriter jsonWriter;

    @Override
    public Object loadItem(String fileOrResourceName) {
        return loadItems(fileOrResourceName).iterator().next();
    }

    @Override
    public void saveItem(String fileName, Object item) {
        saveItems(fileName, new ArrayList(Collections.singletonList(item)));
    }

    public void saveItems(String fileName, Collection items) {
        prepareOutput(fileName);
        try {
            jsonWriter.write(items);
            this.os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.os != null) {
                try {
                    this.os.close();
                    this.os = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            jsonWriter = null;
        }
    }

    public Collection loadItems(String fileOrResourceName) {
        try (InputStream is = getInput(fileOrResourceName)) {
            if (is instanceof ZipInputStream) {
                ZipEntry entry = ((ZipInputStream)is).getNextEntry();
            }
            Collection result = (Collection) JsonReader.jsonToJava(is, null);
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

    private void prepareOutput(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name is not specified");
        }
        FileUtils.prepareFileForReCreation(fileName);
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            if (fileName.endsWith(".zip")) {
                ZipOutputStream zos = new ZipOutputStream(fos);
                zos.setLevel(9);
                int zipIndex = fileName.lastIndexOf(".zip");
                int simpleNameIndex = fileName.lastIndexOf(".", Math.max(0, zipIndex - 1));
                if (simpleNameIndex == -1) {
                    simpleNameIndex = 0;
                }
                zos.putNextEntry(new ZipEntry(fileName.substring(simpleNameIndex, zipIndex)));
                this.os = zos;
            } else {
                this.os = fos;
            }

            this.jsonWriter = new JsonWriter(this.os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
