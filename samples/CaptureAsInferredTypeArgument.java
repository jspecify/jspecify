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
interface CaptureAsInferredTypeArgument {
  interface Lib<T extends @Nullable Object> {}

  <T> void useImplicitlyObjectBounded(Lib<T> l);

  <T extends Object> void useExplicitlyObjectBounded(Lib<T> l);

  <T extends @NullnessUnspecified Object> void useUnspecBounded(Lib<T> l);

  <T extends @Nullable Object> void useUnionNullBounded(Lib<T> l);

  default void objectBounded(Lib<? extends Object> l) {
    useImplicitlyObjectBounded(l);
    useExplicitlyObjectBounded(l);
    useUnspecBounded(l);
    useUnionNullBounded(l);
  }

  default void unspecBounded(Lib<? extends @NullnessUnspecified Object> l) {
    // :: error: jspecify_nullness_not_enough_information 
    useImplicitlyObjectBounded(l);
    // :: error: jspecify_nullness_not_enough_information 
    useExplicitlyObjectBounded(l);
    // :: error: jspecify_nullness_not_enough_information 
    useUnspecBounded(l);
    useUnionNullBounded(l);
  }

  default void implicitlyUnionNullBounded(Lib<?> l) {
    // :: error: jspecify_nullness_mismatch
    useImplicitlyObjectBounded(l);
    // :: error: jspecify_nullness_mismatch
    useExplicitlyObjectBounded(l);
    // :: error: jspecify_nullness_not_enough_information
    useUnspecBounded(l);
    useUnionNullBounded(l);
  }

  default void explicitlyUnionNullBounded(Lib<? extends @Nullable Object> l) {
    // :: error: jspecify_nullness_mismatch
    useImplicitlyObjectBounded(l);
    // :: error: jspecify_nullness_mismatch
    useExplicitlyObjectBounded(l);
    // :: error: jspecify_nullness_not_enough_information
    useUnspecBounded(l);
    useUnionNullBounded(l);
  }
}
