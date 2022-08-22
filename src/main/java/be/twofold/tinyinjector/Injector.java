package be.twofold.tinyinjector;

import java.lang.reflect.*;
import java.util.*;

public final class Injector {

    private static final int UNKNOWN = 0;
    private static final int INSTANTIATING = 1;
    private static final int FINISHED = 2;

    public <T> T getInstance(Class<T> clazz) {
        return getInstance(clazz, new IdentityHashMap<>());
    }

    private <T> T getInstance(Class<T> clazz, Map<Class<?>, Integer> visited) {
        if (visited.getOrDefault(clazz, UNKNOWN) == INSTANTIATING) {
            throw new IllegalStateException("Circular dependency found!");
        }
        visited.put(clazz, INSTANTIATING);

        Constructor<T> constructor = getConstructor(clazz);

        Object[] arguments = Arrays.stream(constructor.getParameters())
            .map(parameter -> (Object) getInstance(parameter.getType(), visited))
            .toArray();

        try {
            T instance = constructor.newInstance(arguments);
            visited.put(clazz, FINISHED); // Successfully instantiated the class
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
