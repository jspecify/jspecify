import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
interface InferenceChoosesNullableTypeVariable {
  <T extends @Nullable Object> void consume(T t);

  default <E> void go(@Nullable E value) {
    consume(value);
  }
}
