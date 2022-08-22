package be.twofold.tinyinjector;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class CyclicDependencyTest {

    @Test
    void testCyclicDependencies() {
        assertThatIllegalStateException()
            .isThrownBy(() -> new Injector().getInstance(A.class))
            .withMessage("Circular dependency found!");
    }

    @SuppressWarnings("FieldCanBeLocal")
    static final class A {
        private final B b;

        public A(B b) {
            this.b = b;
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    static final class B {
        private final C c;

        public B(C c) {
            this.c = c;
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    static final class C {
        private final A a;

        public C(A a) {
            this.a = a;
        }
    }

}
