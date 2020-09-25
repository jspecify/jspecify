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
class IllegalLocationsNotGenericWithNestedUnspec {
  interface Lib<T extends @Nullable Object> {}

  class Nested {
    class DoublyNested {}
  }

  @NullnessUnspecified Nested x4;

  // jspecify_nullness_paradox
  @NullnessUnspecified IllegalLocationsNotGenericWithNestedUnspec.Nested x5;

  IllegalLocationsNotGenericWithNestedUnspec.@NullnessUnspecified Nested x6;

  // jspecify_nullness_paradox
  @NullnessUnspecified IllegalLocationsNotGenericWithNestedUnspec.Nested.DoublyNested x7;

  // jspecify_nullness_paradox
  IllegalLocationsNotGenericWithNestedUnspec.@NullnessUnspecified Nested.DoublyNested x8;

  IllegalLocationsNotGenericWithNestedUnspec.Nested.@NullnessUnspecified DoublyNested x9;

  // jspecify_nullness_paradox
  Lib<@NullnessUnspecified IllegalLocationsNotGenericWithNestedUnspec.Nested.DoublyNested> l1;

  // jspecify_nullness_paradox
  Lib<IllegalLocationsNotGenericWithNestedUnspec.@NullnessUnspecified Nested.DoublyNested> l2;

  Lib<IllegalLocationsNotGenericWithNestedUnspec.Nested.DoublyNested> l3;
}
