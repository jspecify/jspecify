package conformance;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

class Basic {
  @NonNull
  Object cannotConvertNullableToNonNull(@Nullable Object nullable) {
    // test:cannot-convert:Object? to Object2!
    return nullable;
  }
}
