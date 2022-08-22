package be.twofold.tinyinjector;

import jakarta.inject.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public final class Injector {

    private static final int UNKNOWN = 0;
    private static final int INSTANTIATING = 1;
    private static final int FINISHED = 2;

    private final Map<Class<?>, Provider<?>> providers = new IdentityHashMap<>();

    public <T> Injector bind(Class<T> type, Class<? extends T> implementationType) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(implementationType, "implementationType");

        Constructor<? extends T> constructor = getConstructor(implementationType);
        return bindProvider(type, () -> construct(constructor, new IdentityHashMap<>()));
    }

    public <T> Injector bindProvider(Class<T> type, Provider<? extends T> provider) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(provider, "provider");

        if (providers.containsKey(type)) {
            throw new IllegalArgumentException("Provider for " + type + " already bound");
        }

        providers.put(type, provider);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type) {
        Objects.requireNonNull(type, "type");

        if (providers.containsKey(type)) {
            return (T) providers.get(type).get();
        }

        return getInstance(type, new IdentityHashMap<>());
    }

    private <T> T getInstance(Class<T> type, Map<Class<?>, Integer> visited) {
        if (visited.getOrDefault(type, UNKNOWN) == INSTANTIATING) {
            throw new IllegalArgumentException("Circular dependency found!");
        }
        visited.put(type, INSTANTIATING);

        T instance = construct(getConstructor(type), visited);
        visited.put(type, FINISHED);
        return instance;
    }

    private <T> T construct(Constructor<T> constructor, Map<Class<?>, Integer> visited) {
        Object[] arguments = Arrays.stream(constructor.getParameters())
            .map(parameter -> getInstance(parameter.getType(), visited))
            .toArray();

        try {
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new IllegalArgumentException("Something went wrong", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getConstructor(Class<T> clazz) {
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        switch (constructors.length) {
            case 0:
                throw new IllegalArgumentException("No public constructor found");
            case 1:
                return constructors[0];
            default:
                return getAnnotatedConstructor(constructors);
        }
    }

    private <T> Constructor<T> getAnnotatedConstructor(Constructor<T>[] constructors) {
        List<Constructor<T>> annotatedConstructors = Arrays.stream(constructors)
            .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
            .collect(Collectors.toList());

        switch (annotatedConstructors.size()) {
            case 0:
                throw new IllegalArgumentException("Multiple public constructors found");
            case 1:
                return annotatedConstructors.get(0);
            default:
                throw new IllegalArgumentException("Multiple public @Inject annotated constructors found");
        }
    }

}
