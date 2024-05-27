package com.perceptnet.commons.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com); on 024 24.05.2024
 */
public class ClassLoaderUtils {

    public static Map<String, Set<Class<?>>> collectClasses(String packageName, boolean recursively, Set<String> annotationNames) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();
            Map<String, Set<Class<?>>> result = new LinkedHashMap<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equalsIgnoreCase("jar")) {
                    collectClassesFromJar(result, resource, packageName, recursively, annotationNames);
                } else {
                    dirs.add(new File(resource.getFile()));
                }
            }
            for (File directory : dirs) {
                collectClassesFromDir(result, directory, packageName, recursively, annotationNames);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Cannot scan available classes of package " + packageName + " due to " + e, e);
        }
    }

    private static void collectClassesFromDir(Map<String, Set<Class<?>>> registry, File directory, String packageName,
                                              boolean recursively, Set<String> annotationNames) throws ClassNotFoundException {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                if (recursively && !file.getName().contains(".")) {
                    collectClassesFromDir(registry, file, packageName + "." + file.getName(), recursively, annotationNames);
                }
            } else if (file.getName().endsWith(".class")) {
                Class<?> clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                if (clazz.isInterface() ||
                        (annotationNames == null || annotationNames.isEmpty() ||
                                Arrays.stream(clazz.getAnnotations()).anyMatch(anno -> annotationNames.contains(anno.annotationType().getName())))) {
                    registry.computeIfAbsent(packageName, k -> new LinkedHashSet<>()).add(clazz);
                }
            }
        }
    }

    private static void collectClassesFromJar(Map<String, Set<Class<?>>> registry,
                                              URL jarResource, String packageName, boolean recursively,
                                              Set<String> annotationNames) throws IOException, ClassNotFoundException, URISyntaxException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(jarResource.toURI(), Collections.emptyMap())) {
            Path resPath = fileSystem.getPath("/");
            Files.walk(resPath, Integer.MAX_VALUE).forEach(path -> {
                String fileName = path.toString();
                if (fileName.startsWith("/")) {
                    fileName = fileName.substring(1);
                }
                if (fileName.endsWith(".class")) {
                    String qualifiedClassName = fileName.replace("/", ".").substring(0, fileName.lastIndexOf("."));
                    String classPackageName = qualifiedClassName.substring(0, qualifiedClassName.lastIndexOf("."));
                    if (classPackageName.equals(packageName) || recursively && classPackageName.startsWith(packageName)) {
                        try {
                            Class<?> clazz = Class.forName(qualifiedClassName);
                            if (clazz.isInterface() ||
                                    (annotationNames == null || annotationNames.isEmpty() ||
                                            Arrays.stream(clazz.getAnnotations()).anyMatch(
                                                    an -> annotationNames.contains(an.annotationType().getName())))) {
                                registry.computeIfAbsent(classPackageName, k -> new LinkedHashSet<>()).add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
