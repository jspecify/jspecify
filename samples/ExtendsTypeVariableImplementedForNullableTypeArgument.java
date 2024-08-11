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
class ExtendsTypeVariableImplementedForNullableTypeArgument {
  interface Filter<T extends @Nullable Object> {
    boolean filterOne(T t);

    <U extends T> Sequence<U> filterMany(Sequence<U> in);
  }

  interface Sub0 extends Filter<@Nullable Object> {
    // :: error: jspecify_nullness_mismatch jspecify_but_expect_nothing
    <U> Sequence<U> filterMany(Sequence<U> in);
  }

  interface Sub1 extends Filter<@Nullable Object> {
    // :: error: jspecify_nullness_mismatch jspecify_but_expect_nothing
    <U extends Object> Sequence<U> filterMany(Sequence<U> in);
  }

  interface Sub2 extends Filter<@Nullable Object> {
    // :: error: jspecify_nullness_not_enough_information jspecify_but_expect_nothing
    <U extends @NullnessUnspecified Object> Sequence<U> filterMany(Sequence<U> in);
  }

  interface Sub3 extends Filter<@Nullable Object> {
    <U extends @Nullable Object> Sequence<U> filterMany(Sequence<U> in);
  }

  interface Sequence<T extends @Nullable Object> {}
}
