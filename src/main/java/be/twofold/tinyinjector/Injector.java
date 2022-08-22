package be.twofold.tinyinjector;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public final class Injector {

    private final Set<Class<?>> requestedClasses = new HashSet<>();
    private final Set<Class<?>> instantiableClasses = new HashSet<>();

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();

        if (constructors.length != 1) {
            throw new IllegalStateException("Only one constructor allowed!");
        }

        Constructor<T> constructor = constructors[0];

        if (requestedClasses.contains(clazz)) {
            if (!instantiableClasses.contains(clazz)) {
                throw new IllegalStateException("");
            }
        } else {
            requestedClasses.add(clazz);
        }

        List<Object> arguments = Arrays.stream(constructor.getParameters())
            .map(parameter -> (Object) getInstance(parameter.getType()))
            .collect(Collectors.toList());

        try {
            T instance = constructor.newInstance(arguments.toArray());
            instantiableClasses.add(clazz); // mark the class as successfully instantiable.
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("Something went wrong :-(");
    }

}
