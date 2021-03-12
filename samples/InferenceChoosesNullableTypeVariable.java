import org.jspecify.nullness.DefaultNonNull;
import org.jspecify.nullness.Nullable;

@DefaultNonNull
interface InferenceChoosesNullableTypeVariable {
  <T extends @Nullable Object> void consume(T t);

  default <E> void go(@Nullable E value) {
    consume(value);
  }
}
