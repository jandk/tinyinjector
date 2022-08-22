package be.twofold.tinyinjector;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class InjectorTest {

    private final Injector injector = new Injector();

    @Test
    void testInstantiation() {
        A instance = injector.getInstance(A.class);
        assertThat(instance).isNotNull();
        assertThat(instance.b).isNotNull();
        assertThat(instance.b.c).isNotNull();
    }

    @Test
    void testProviderThrowsOnNull() {
        assertThatNullPointerException()
            .isThrownBy(() -> injector.bindProvider(Object.class, null));
        assertThatNullPointerException()
            .isThrownBy(() -> injector.bindProvider(null, () -> null));
    }

    @Test
    void testProviderThrowsOnDuplicateDefinition() {
        injector.bindProvider(A.class, () -> null);
        assertThatIllegalArgumentException()
            .isThrownBy(() -> injector.bindProvider(A.class, () -> null))
            .withMessage("Provider for " + A.class + " already bound");
    }

    @Test
    void testProvider() {
        injector.bindProvider(CharSequence.class, () -> "Hello");
        assertThat(injector.getInstance(CharSequence.class)).isEqualTo("Hello");
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
        public C() {
        }
    }

}
