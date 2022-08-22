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
    void testBindThrowsOnNull() {
        assertThatNullPointerException()
            .isThrownBy(() -> injector.bind(null, A.class));
        assertThatNullPointerException()
            .isThrownBy(() -> injector.bind(A.class, null));
    }

    @Test
    void testBindThrowsOnAlreadyBound() {
        injector.bind(A.class, D.class);
        assertThatIllegalArgumentException()
            .isThrownBy(() -> injector.bind(A.class, D.class));
    }

    @Test
    void testBind() {
        injector.bind(A.class, D.class);
        assertThat(injector.getInstance(A.class))
            .isInstanceOf(D.class);
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
    static class A {
        private final B b;

        public A(B b) {
            this.b = b;
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    static class B {
        private final C c;

        public B(C c) {
            this.c = c;
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    static class C {
        public C() {
        }
    }

    static class D extends A {
        public D(B b) {
            super(b);
        }
    }

}
