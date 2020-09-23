/*
 * Copyright 2020 The jspecify Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jspecify.annotations.DefaultNonNull;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullnessUnspecified;

class NotNullAwareUnboxing {
  int x0(IntegerSupplier i) {
    return SafeGets.get(i);
  }

  int x1(IntegerUnspecSupplier i) {
    // jspecify_nullness_not_enough_information
    return SafeGets.get(i);
  }

  int x2(IntegerUnionNullSupplier i) {
    // jspecify_nullness_mismatch
    return SafeGets.get(i);
  }

  long x3(IntegerSupplier i) {
    return SafeGets.get(i);
  }

  long x4(IntegerUnspecSupplier i) {
    // jspecify_nullness_not_enough_information
    return SafeGets.get(i);
  }

  long x5(IntegerUnionNullSupplier i) {
    // jspecify_nullness_mismatch
    return SafeGets.get(i);
  }

  @DefaultNonNull
  interface IntegerSupplier {
    Integer get();
  }

  @DefaultNonNull
  interface IntegerUnspecSupplier {
    @NullnessUnspecified
    Integer get();
  }

  @DefaultNonNull
  interface IntegerUnionNullSupplier {
    @Nullable
    Integer get();
  }

  @DefaultNonNull
  static class SafeGets {
    // TODO(cpovirk): Avoid conditional logic, which checkers might not recognize.

    static Integer get(@Nullable IntegerSupplier s) {
      return s == null ? 0 : s.get();
    }

    static @NullnessUnspecified Integer get(@Nullable IntegerUnspecSupplier s) {
      return s == null ? 0 : s.get();
    }

    static @Nullable Integer get(@Nullable IntegerUnionNullSupplier s) {
      return s == null ? 0 : s.get();
    }
  }
}
