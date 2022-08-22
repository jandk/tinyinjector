package be.twofold.tinyinjector;

import java.lang.reflect.*;
import java.util.*;

public final class Injector {

    private final Set<Class<?>> requestedClasses = new HashSet<>();
    private final Set<Class<?>> instantiableClasses = new HashSet<>();

    public <T> T getInstance(Class<T> clazz) {
        Constructor<T> constructor = getConstructor(clazz);

        if (requestedClasses.contains(clazz)) {
            if (!instantiableClasses.contains(clazz)) {
                throw new IllegalStateException("");
            }
        } else {
            requestedClasses.add(clazz);
        }

        Object[] arguments = Arrays.stream(constructor.getParameters())
            .map(parameter -> (Object) getInstance(parameter.getType()))
            .toArray();

        try {
            T instance = constructor.newInstance(arguments);
            instantiableClasses.add(clazz); // mark the class as successfully instantiable.
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("Something went wrong :-(");
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getConstructor(Class<T> clazz) {
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        if (constructors.length != 1) {
            throw new IllegalStateException("Only one constructor allowed!");
        }

        return constructors[0];
    }

}
