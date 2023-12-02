package org.example;

import org.reflections.Reflections;

import java.util.*;

public class Container {

    private static Object checkAndReturnConstructor(Class<?> type) throws Exception {
        if (type.isInterface()) {
            return searchImpl(type, "org.example");
        }
        return type.getConstructor().newInstance();
    }

    private static Object searchImpl(Class<?> classAbs, String packageImpl) throws Exception {
        return new Reflections(packageImpl).getSubTypesOf(classAbs).stream()
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getConstructor().newInstance();
    }
}
