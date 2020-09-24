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

@DefaultNonNull
// jspecify_nullness_paradox
class IllegalLocations<@Nullable E> {
  interface Lib<T extends @Nullable Object> {}

  // jspecify_nullness_paradox
  Lib<@Nullable ?> x1;

  // jspecify_nullness_paradox
  Lib<@Nullable ? extends Object> x2;

  // jspecify_nullness_paradox
  Lib<@Nullable ? super Object> x3;

  class Nested {}

  @Nullable Nested x4;

  // jspecify_nullness_paradox
  @Nullable IllegalLocations<?>.Nested x5;

  IllegalLocations<?>.@Nullable Nested x6;
}
