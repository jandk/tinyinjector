package be.twofold.tinyinjector;

import jakarta.inject.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class SingletonTest {

    private final Injector injector = new Injector();

    @Test
    void testSingletonSameInstance() {
        injector.bind(A.class, B.class);
        A instance = injector.getInstance(A.class);
        assertThat(injector.getInstance(A.class)).isSameAs(instance);
    }

    @Test
    void testNotSingletonNotSameInstance() {
        injector.bind(A.class, C.class);
        A instance = injector.getInstance(A.class);
        assertThat(injector.getInstance(A.class)).isNotSameAs(instance);
    }

    interface A {
    }

    @Singleton
    static class B implements A {
        public B() {
        }
    }

    static class C implements A {
        public C() {
        }
    }

}
