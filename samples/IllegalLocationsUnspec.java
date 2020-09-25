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
// jspecify_nullness_paradox
class IllegalLocationsUnspec<@NullnessUnspecified E> {
  interface Lib<T extends @Nullable Object> {}

  class Nested {
    class DoublyNested {}
  }

  // jspecify_nullness_paradox
  Lib<@NullnessUnspecified ?> x1;

  // jspecify_nullness_paradox
  Lib<@NullnessUnspecified ? extends Object> x2;

  // jspecify_nullness_paradox
  Lib<@NullnessUnspecified ? super Object> x3;

  @NullnessUnspecified Nested x4;

  // jspecify_nullness_paradox
  @NullnessUnspecified IllegalLocationsUnspec<?>.Nested x5;

  IllegalLocationsUnspec<?>.@NullnessUnspecified Nested x6;

  // jspecify_nullness_paradox
  @NullnessUnspecified IllegalLocationsUnspec<?>.Nested.DoublyNested x7;

  // jspecify_nullness_paradox
  IllegalLocationsUnspec<?>.@NullnessUnspecified Nested.DoublyNested x8;

  IllegalLocationsUnspec<?>.Nested.@NullnessUnspecified DoublyNested x9;

  // jspecify_nullness_paradox
  Lib<@NullnessUnspecified IllegalLocationsUnspec<?>.Nested.DoublyNested> l1;

  // jspecify_nullness_paradox
  Lib<IllegalLocationsUnspec<?>.@NullnessUnspecified Nested.DoublyNested> l2;

  Lib<IllegalLocationsUnspec<?>.Nested.DoublyNested> l3;
}
