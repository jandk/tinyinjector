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

        Provider<T> provider = () -> getInstance(implementationType);
        if (implementationType.isAnnotationPresent(Singleton.class)) {
            provider = new SingletonProvider<>(provider);
        }
        return bindProvider(type, provider);
    }

    public <T> Injector bindInstance(Class<T> type, T instance) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(instance, "instance");

        return bindProvider(type, () -> instance);
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

    public <T> T getInstance(Class<T> type) {
        Objects.requireNonNull(type, "type");

        return getInstance(type, new IdentityHashMap<>());
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstance(Class<T> type, Map<Class<?>, Integer> visited) {
        if (visited.getOrDefault(type, UNKNOWN) == INSTANTIATING) {
            throw new IllegalArgumentException("Circular dependency found!");
        }
        visited.put(type, INSTANTIATING);

        if (providers.containsKey(type)) {
            T instance = (T) providers.get(type).get();
            visited.put(type, FINISHED);
            return instance;
        }

        Constructor<T> constructor = getConstructor(type);
        Object[] arguments = Arrays.stream(constructor.getParameters())
            .map(parameter -> getInstance(parameter.getType(), visited))
            .toArray();

        try {
            T instance = constructor.newInstance(arguments);
            visited.put(type, FINISHED);
            return instance;
        } catch (Exception e) {
            throw new IllegalArgumentException("Something went wrong", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getConstructor(Class<T> type) {
        Constructor<T>[] constructors = (Constructor<T>[]) type.getConstructors();
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

    private static final class SingletonProvider<T> implements Provider<T> {
        private final Provider<? extends T> provider;
        private T instance;

        public SingletonProvider(Provider<? extends T> provider) {
            this.provider = provider;
        }

        @Override
        public T get() {
            if (instance == null) {
                instance = provider.get();
            }
            return instance;
        }
    }

}
