package be.twofold.tinyinjector;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class ConstructorTest {

    private final Injector injector = new Injector();

    @Test
    void testNoConstructor() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> injector.getInstance(NoConstructor.class))
            .withMessage("No public constructor found");
    }

    @Test
    void testSingleConstructor() {
        assertThat(injector.getInstance(SingleConstructor.class))
            .isInstanceOf(SingleConstructor.class);
    }

    @Test
    void testMultipleConstructors() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> injector.getInstance(MultipleConstructors.class))
            .withMessage("Multiple public constructors found");
    }

    public static final class NoConstructor {
        private NoConstructor() {
        }
    }

    public static final class SingleConstructor {
        public SingleConstructor() {
        }
    }

    public static final class MultipleConstructors {
        public MultipleConstructors() {
        }

        public MultipleConstructors(Object o) {
        }
    }

}
