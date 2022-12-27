import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

class NotNullMarkedInferenceChoosesNullableTypeVariable {
  @NullMarked
  interface Super {
    <T extends @Nullable Object> void consume(T t);
  }

  abstract class Sub implements Super {
    <E> void go(@Nullable E value) {
      consume(value);
    }
  }
}
