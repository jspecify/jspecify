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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
class ContainmentSuperVsExtendsSameType {
  void x() {
    // :: error: jspecify_nullness_mismatch jspecify_but_expect_nothing
    new Check<Lib<? extends Number>, Lib<? super Number>>();
  }

  interface Lib<T extends @Nullable Object> {}

  static class Check<F extends @Nullable Object, A extends F> {}
}
