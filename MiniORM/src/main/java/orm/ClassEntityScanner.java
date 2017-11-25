package orm;

import annotations.Entity;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassEntityScanner {
    private Map<String, Class> foundEntities = new HashMap<>();

    public Map<String, Class> listFilesForFolder(String classpath) {
        File currFolder = new File(classpath);
        File[] listOfFiles = currFolder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getAbsolutePath().endsWith(".class")) {
                String currPath = file.getAbsolutePath().replace('\\', '.').replace(".class", "");
                Class clazz = null;
                do {
                    try {
                        clazz = Class.forName(currPath);
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        Object instance = constructor.newInstance();
                        if (instance.getClass().isAnnotationPresent(Entity.class)) {
                            this.foundEntities.put(instance.getClass().getSimpleName(), instance.getClass());
                        }
                    } catch (Exception e) {
                        currPath = cutPath(currPath);
                    }
                } while (clazz == null && currPath != null);

            } else if (file.isDirectory()) {
                listFilesForFolder(file.getAbsolutePath());
            }
        }
        return this.foundEntities;
    }

    public static String cutPath(String path) {
        int substringLength = path.indexOf('.');
        if (substringLength < 0) {
            return null;
        }
        path = path.substring(substringLength + 1, path.length());
        return path;
    }

    public Map<String, Class> getFoundEntities() {
        return this.foundEntities;
    }
}
