package be.twofold.tinyinjector;

import jakarta.inject.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class InjectAnnotationTest {

    private final Injector injector = new Injector();

    @Test
    void testSingleInjectAnnotation() {
        assertThat(injector.getInstance(SingleInject.class))
            .isInstanceOf(SingleInject.class);
    }

    @Test
    void testMultipleInjectAnnotations() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> injector.getInstance(MultipleInjects.class))
            .withMessage("Multiple public @Inject annotated constructors found");
    }

    public static class SingleInject {
        @Inject
        public SingleInject() {
        }

        public SingleInject(Object o) {
        }
    }

    public static class MultipleInjects {
        @Inject
        public MultipleInjects() {
        }

        @Inject
        public MultipleInjects(Object o) {
        }
    }

}
