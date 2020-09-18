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

import org.jspecify.annotations.Nullable;

// PARADOX
class NotNullAwareIllegalLocations<@Nullable E> {
  interface Lib<T extends @Nullable Object> {}

  // PARADOX
  Lib<@Nullable ?> x1;

  // PARADOX
  Lib<@Nullable ? extends Object> x2;

  // PARADOX
  Lib<@Nullable ? super Object> x3;

  class Nested {}

  @Nullable Nested x4;

  // PARADOX
  @Nullable NotNullAwareIllegalLocations<?>.Nested x5;

  NotNullAwareIllegalLocations<?>.@Nullable Nested x6;
}
