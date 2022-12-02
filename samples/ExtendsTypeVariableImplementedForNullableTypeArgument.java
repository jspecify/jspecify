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
  interface NullableBounded<T extends @Nullable Object> {
    <U extends T> void x(U u);

    interface Sub0 extends NullableBounded<@Nullable Object> {
      // jspecify_nullness_mismatch
      <U> void x(U u);
    }

    interface Sub1 extends NullableBounded<@Nullable Object> {
      // jspecify_nullness_mismatch
      <U extends Object> void x(U u);
    }

    interface Sub2 extends NullableBounded<@Nullable Object> {
      // jspecify_nullness_not_enough_information
      <U extends @NullnessUnspecified Object> void x(U u);
    }

    interface Sub3 extends NullableBounded<@Nullable Object> {
      <U extends @Nullable Object> void x(U u);
    }
  }
}
