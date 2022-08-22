package be.twofold.tinyinjector;

import jakarta.inject.*;

import java.util.*;

final class SingletonProvider<T> implements Provider<T> {

    private final Provider<? extends T> provider;
    private T instance;

    SingletonProvider(Provider<? extends T> provider) {
        this.provider = Objects.requireNonNull(provider, "provider");
    }

    @Override
    public T get() {
        if (instance == null) {
            instance = provider.get();
        }
        return instance;
    }

}
