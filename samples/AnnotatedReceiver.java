/*
 * Copyright 2020 The JSpecify Authors.
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

@DefaultNonNull
public class AnnotatedReceiver<T extends @Nullable Object, U> {

  void foo(AnnotatedReceiver<T, U> this) {
    // jspecify_nullness_mismatch
    nullableReceiverTypeParameter();
    // jspecify_nullness_not_enough_information
    unspecifiedReceiverTypeParameter();
  }

  // jspecify_unrecognized_location
  void nullableReceiverInvalid(@Nullable AnnotatedReceiver<T, U> this) {
    this.foo();
  }

  // jspecify_unrecognized_location
  void unspecifiedReceiverInvalid(@NullnessUnspecified AnnotatedReceiver<T, U> this) {
    this.foo();
  }

  void nullableReceiverTypeParameter(AnnotatedReceiver<@Nullable T, U> this) {
    this.foo();
  }

  // jspecify_nullness_mismatch
  void illformedNullableReceiverTypeParameter(AnnotatedReceiver<T, @Nullable U> this) {}

  void unspecifiedReceiverTypeParameter(AnnotatedReceiver<@NullnessUnspecified T, U> this) {
    this.foo();
  }

  void coercedUnspecifiedReceiverTypeParameter(AnnotatedReceiver<T, @NullnessUnspecified U> this) {
    // jspecify_nullness_not_enough_information
    this.foo();
  }
}
