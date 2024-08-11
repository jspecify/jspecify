/*
 * Copyright 2022 The JSpecify Authors.
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullnessUnspecified;

@NullMarked
abstract class ExtendsSameType {
  interface Foo {}

  interface Lib<T> {}

  interface Supplier<T extends @Nullable Object> {}

  abstract void use(Lib<Supplier<? extends Foo>> l);

  abstract void useUnspec(Lib<Supplier<? extends @NullnessUnspecified Foo>> l);

  abstract void useUnionNull(Lib<Supplier<? extends @Nullable Foo>> l);

  void client(Lib<Supplier<? extends Foo>> l) {
    use(l);
    // :: error: jspecify_nullness_not_enough_information jspecify_but_expect_nothing
    useUnspec(l);
    // :: error: jspecify_nullness_mismatch jspecify_but_expect_nothing
    useUnionNull(l);
  }

  void clientUnspec(Lib<Supplier<? extends @NullnessUnspecified Foo>> l) {
    // :: error: jspecify_nullness_not_enough_information jspecify_but_expect_nothing
    use(l);
    // :: error: jspecify_nullness_not_enough_information jspecify_but_expect_nothing
    useUnspec(l);
    // :: error: jspecify_nullness_not_enough_information jspecify_but_expect_nothing
    useUnionNull(l);
  }

  void clientUnionNull(Lib<Supplier<? extends @Nullable Foo>> l) {
    // :: error: jspecify_nullness_mismatch jspecify_but_expect_nothing
    use(l);
    // :: error: jspecify_nullness_not_enough_information jspecify_but_expect_nothing
    useUnspec(l);
    useUnionNull(l);
  }
}
