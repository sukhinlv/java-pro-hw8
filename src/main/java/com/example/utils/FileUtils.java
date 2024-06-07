package com.example.utils;


import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {

    private FileUtils() {
    }

    public static String loadFileFromClasspath(String path) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream(path)) {
            if (is != null) {
                return new String(is.readAllBytes());
            }
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
